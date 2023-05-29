package org.noear.solon.aot;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.aot.hint.ResourceHint;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ScanUtil;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * aot 运行的启动类，用于生成 native 元数据
 *
 * @author songyinyin
 * @since 2023/4/11 14:11
 */
public class SolonAotProcessor {
    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);

    private final Settings settings;

    private final String[] applicationArgs;

    private final Class<?> applicationClass;

    public SolonAotProcessor(Settings settings, String[] applicationArgs, Class<?> applicationClass) {
        this.settings = settings;
        this.applicationArgs = applicationArgs;
        this.applicationClass = applicationClass;
    }

    public static void main(String[] args) throws Exception {

        LogUtil.global().info("Aot processor start, args: " + Arrays.toString(args));

        int requiredArgs = 5;
        if (args.length < requiredArgs) {
            throw new IllegalArgumentException("Usage: " + SolonAotProcessor.class.getName()
                    + " <applicationName> <classOutput> <generatedSources> <groupId> <artifactId> <originalArgs...>");
        }

        Class<?> application = Class.forName(args[0]);
        Settings build = new Settings(Paths.get(args[1]), Paths.get(args[2]), args[3], args[4]);

        String[] applicationArgs = (args.length > requiredArgs) ? Arrays.copyOfRange(args, requiredArgs, args.length)
                : new String[0];

        new SolonAotProcessor(build, applicationArgs, application).process();
    }

    public final void process() {
        try {
            System.setProperty(NativeDetector.AOT_PROCESSING, "true");
            doProcess();
        } finally {
            System.clearProperty(NativeDetector.AOT_PROCESSING);
        }
    }

    protected void doProcess() {
        try {
            Method mainMethod = applicationClass.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{this.applicationArgs});
        } catch (Exception e) {
            e.printStackTrace();
        }


        AopContext context = Solon.app().context();

        //改为普通扩展方式
        AopContextNativeProcessor contextNativeProcessor = context.getBean(AopContextNativeProcessor.class);
        if(contextNativeProcessor == null){
            contextNativeProcessor = new AopContextNativeProcessorDefault();
        }


        RuntimeNativeMetadata metadata = new RuntimeNativeMetadata();
        metadata.setApplicationClassName(applicationClass.getCanonicalName());

        //处理 bean（生成配置、代理等...）
        contextNativeProcessor.process(context, settings, metadata);

        List<PluginEntity> plugs = Solon.cfg().plugs();
        for (PluginEntity plug : plugs) {
            if (Utils.isNotEmpty(plug.getClassName())) {
                //手动注册时，没有字符串类名
                metadata.registerDefaultConstructor(plug.getClassName());
            }
        }

        List<RuntimeNativeRegistrar> runtimeNativeRegistrars = context.getBeansOfType(RuntimeNativeRegistrar.class);
        for (RuntimeNativeRegistrar runtimeNativeRegistrar : runtimeNativeRegistrars) {
            runtimeNativeRegistrar.register(context, metadata);
        }


        addNativeImage(metadata);

        // 添加 resource-config.json
        addResourceConfig(metadata);
        // 添加 reflect-config.json
        addReflectConfig(metadata);
        // 添加 serialization-config.json
        addSerializationConfig(metadata);
        // 添加 proxy-config.json
        addJdkProxyConfig(metadata);

        LogUtil.global().info("Aot processor end.");

        // 正常退出
        Solon.stopBlock(true, -1, 0);
    }


    private void addSerializationConfig(RuntimeNativeMetadata metadata) {
        String serializationJson = metadata.toSerializationJson();
        if (Utils.isEmpty(serializationJson)) {
            return;
        }
        try {
            FileWriter fileWriter = getFileWriter("serialization-config.json");
            fileWriter.write(serializationJson);
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addJdkProxyConfig(RuntimeNativeMetadata metadata) {
        String jdkProxyJson = metadata.toJdkProxyJson();
        if (Utils.isEmpty(jdkProxyJson)) {
            return;
        }
        try {
            FileWriter fileWriter = getFileWriter("proxy-config.json");
            fileWriter.write(jdkProxyJson);
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加 native-image.properties
     */
    private void addNativeImage(RuntimeNativeMetadata metadata) {
        try {
            Set<String> args = getDefaultNativeImageArguments(metadata.getApplicationClassName());
            args.addAll(metadata.getArgs());

            StringBuilder sb = new StringBuilder();
            sb.append("Args = ");
            sb.append(String.join(String.format(" \\%n"), args));

            FileWriter fileWriter = getFileWriter("native-image.properties");
            fileWriter.write(sb.toString());
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加 resource-config.json，同时将扫描到的文件，写入solon-resource.json中，方便 native 模式下，扫描资源
     *
     * @see GraalvmUtil#scanResource(String, Predicate, Set)
     */
    private void addResourceConfig(RuntimeNativeMetadata metadata) {
        try {
            metadata.registerResourceInclude("app.*\\.yml")
                    .registerResourceInclude("app.*\\.properties")
                    .registerResourceInclude("META-INF/.*")
                    .registerResourceInclude("WEB-INF/.*")
                    .registerResourceInclude("static/.*")
                    .registerResourceInclude("templates/.*");

            List<ResourceHint> includes = metadata.getIncludes();
            List<String> allResources = new ArrayList<>();
            for (ResourceHint include : includes) {
                int pathIdx = include.getPattern().indexOf("/");
                if (pathIdx > 0) {
                    String pathDir = include.getPattern().substring(0, pathIdx);
                    if (pathDir.contains("*") == false) {
                        Pattern pattern = Pattern.compile(include.getPattern().substring(pathIdx + 1));
                        Set<String> scanned = ScanUtil.scan(pathDir, path -> pattern.matcher(path).find());
                        if (!scanned.isEmpty()) {
                            for (String uri : scanned) {
                                if (uri.startsWith("META-INF/maven/") == false) {
                                    allResources.add(uri);
                                }
                            }
                        }
                    }
                }
            }

            FileWriter solonResourceFile = getFileWriter(GraalvmUtil.SOLON_RESOURCE_NAME);
            solonResourceFile.write(ONode.load(allResources, jsonOptions).toJson());
            solonResourceFile.close();

            FileWriter fileWriter = getFileWriter("resource-config.json");
            fileWriter.write(metadata.toResourcesJson());
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加 reflect-config.json
     */
    private void addReflectConfig(RuntimeNativeMetadata metadata) {
        try {
            addReflectConfigDo(metadata, "org.noear.solon.extend.impl.PropsLoaderExt");
            addReflectConfigDo(metadata, "org.noear.solon.extend.impl.PropsConverterExt");
            addReflectConfigDo(metadata, "org.noear.solon.extend.impl.AppClassLoaderExt");
            addReflectConfigDo(metadata, "org.noear.solon.extend.impl.ReflectionExt");
            addReflectConfigDo(metadata, "org.noear.solon.extend.impl.ResourceScannerExt");

            FileWriter fileWriter = getFileWriter("reflect-config.json");
            fileWriter.write(metadata.toReflectionJson());
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void addReflectConfigDo(RuntimeNativeMetadata metadata, String className) {
        if (ClassUtil.loadClass(className) != null) {
            metadata.registerDefaultConstructor(className);
        }
    }

    private Set<String> getDefaultNativeImageArguments(String applicationClassName) {
        Set<String> args = new TreeSet<>();
        args.add("-H:Class=" + applicationClassName);
        args.add("--report-unsupported-elements-at-runtime");
        args.add("--no-fallback");
        args.add("--install-exit-handlers");

        return args;
    }

    private FileWriter getFileWriter(String configName) {
        try {
            String dir = GraalvmUtil.getNativeImageDir();
            String fileName = String.join("/", dir, configName);

            File file = new File(settings.getClassOutput() + "/" + fileName);
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }

            boolean newFile = file.createNewFile();
            if (newFile) {
                LogUtil.global().info("create file: " + file.getAbsolutePath());
            }
            return new FileWriter(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isEmpty(Object[] objects) {
        return objects == null || objects.length == 0;
    }

    private boolean isNotEmpty(Object[] objects) {
        return !isEmpty(objects);
    }
}
