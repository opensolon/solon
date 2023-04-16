package org.noear.solon.aot;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.ResourceHint;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ScanUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * aot 运行的启动类，用于生成 native 元数据
 *
 * @author songyinyin
 * @since 2023/4/11 14:11
 */
public class SolonAppAotProcessor {

    public static final String AOT_PROCESSING = "solon.aot.processing";

    private AopContextNativeProcessor aopContextNativeProcessor;

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);

    private final Settings settings;

    private final String[] applicationArgs;

    private final Class<?> applicationClass;

    private static final List<String> ALLOW_RESOURCES = Arrays.asList("META-INF", "static", "templates", "sql");

    public SolonAppAotProcessor(Settings settings, String[] applicationArgs, Class<?> applicationClass) {
        this.settings = settings;
        this.applicationArgs = applicationArgs;
        this.applicationClass = applicationClass;
    }

    public static void main(String[] args) throws Exception {

        LogUtil.global().info("aot processor start, args: " + Arrays.toString(args));

        int requiredArgs = 4;
        if (args.length < requiredArgs) {
            throw new IllegalArgumentException("Usage: " + SolonAppAotProcessor.class.getName()
                    + " <applicationName> <classOutput> <groupId> <artifactId> <originalArgs...>");
        }

        Class<?> application = Class.forName(args[0]);
        Settings build = new Settings(Paths.get(args[1]), args[2], args[3]);

        String[] applicationArgs = (args.length > requiredArgs) ? Arrays.copyOfRange(args, requiredArgs, args.length)
                : new String[0];

        new SolonAppAotProcessor(build, applicationArgs, application).process();
    }

    public final void process() {
        try {
            System.setProperty(AOT_PROCESSING, "true");
            doProcess();
        } finally {
            System.clearProperty(AOT_PROCESSING);
        }
    }

    protected void doProcess() {
        try {
            Method mainMethod = applicationClass.getMethod("main", String[].class);
            mainMethod.invoke(null, new Object[]{this.applicationArgs});
        } catch (Exception e) {
            e.printStackTrace();
        }

        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        AopContextNativeProcessor ext = ClassUtil.newInstance("org.noear.solon.extend.impl.AopContextNativeProcessorExt");
        if (ext != null) {
            aopContextNativeProcessor = ext;
        } else {
            aopContextNativeProcessor = new DefaultAopContextNativeProcessor();
        }

        AopContext context = Solon.app().context();

        RuntimeNativeMetadata nativeMetadata = new RuntimeNativeMetadata();
        nativeMetadata.setApplicationClassName(applicationClass.getCanonicalName());
        nativeMetadata.setPackageName(settings.groupId + "." + settings.artifactId);

        processBean(context, nativeMetadata);

        List<PluginEntity> plugs = Solon.cfg().plugs();
        for (PluginEntity plug : plugs) {
            nativeMetadata.registerDefaultConstructor(plug.getClassName());
        }

        List<RuntimeNativeRegistrar> runtimeNativeRegistrars = context.getBeansOfType(RuntimeNativeRegistrar.class);
        for (RuntimeNativeRegistrar runtimeNativeRegistrar : runtimeNativeRegistrars) {
            runtimeNativeRegistrar.register(context, nativeMetadata);
        }


        addNativeImage(nativeMetadata);

        // 添加 resource-config.json
        addResourceConfig(nativeMetadata);
        // 添加 reflect-config.json
        addReflectConfig(nativeMetadata);
        // 添加 serialization-config.json
        addSerializationConfig(nativeMetadata);

        LogUtil.global().info("aot processor end.");
        Solon.stopBlock(false, -1);
    }

    private void processBean(AopContext context, RuntimeNativeMetadata nativeMetadata) {
        AtomicInteger beanCount = new AtomicInteger();
        context.beanForeach(beanWrap -> {
            // aot阶段产生的bean，不需要注册到 native 元数据里
            if (RuntimeNativeRegistrar.class.isAssignableFrom(beanWrap.clz())) {
                return;
            }
            beanCount.getAndIncrement();
            if (beanWrap.clzInit() != null) {
                nativeMetadata.registerMethod(beanWrap.clzInit(), ExecutableMode.INVOKE);
            } else {
                nativeMetadata.registerDefaultConstructor(beanWrap.clz());
            }
            aopContextNativeProcessor.processBean(nativeMetadata, beanWrap);

            ClassWrap clzWrap = ClassWrap.get(beanWrap.clz());
            Map<String, FieldWrap> fieldAllWraps = clzWrap.getFieldAllWraps();
            for (FieldWrap fieldWrap : fieldAllWraps.values()) {
                aopContextNativeProcessor.processField(nativeMetadata, fieldWrap);
            }
        });

        context.methodForeach(methodWrap -> {
            aopContextNativeProcessor.processMethod(nativeMetadata, methodWrap);
        });
        LogUtil.global().info("aot process bean, bean size: " + beanCount.get());
    }

    private void addSerializationConfig(RuntimeNativeMetadata nativeMetadata) {
        String serializationJson = nativeMetadata.toSerializationJson();
        if (Utils.isEmpty(serializationJson)) {
            return;
        }
        try {
            FileWriter fileWriter = getFileWriter(nativeMetadata, "serialization-config.json");
            fileWriter.write(serializationJson);
            fileWriter.close();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    /**
     * 添加 native-image.properties
     */
    private void addNativeImage(RuntimeNativeMetadata nativeMetadata) {
        try {
            List<String> args = getDefaultNativeImageArguments(nativeMetadata.getApplicationClassName());
            StringBuilder sb = new StringBuilder();
            sb.append("Args = ");
            sb.append(String.join(String.format(" \\%n"), args));

            FileWriter fileWriter = getFileWriter(nativeMetadata, "native-image.properties");
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
    private void addResourceConfig(RuntimeNativeMetadata nativeMetadata) {
        try {
            nativeMetadata.registerResourceInclude("app.*\\.yml")
                    .registerResourceInclude("app.*\\.properties")
                    .registerResourceInclude("META-INF/solon/.*\\.json")
                    .registerResourceInclude("META-INF/solon/.*\\.properties")
                    .registerResourceInclude("META-INF/solon_def/.*\\.txt")
                    .registerResourceInclude("META-INF/solon_def/.*\\.xml")
                    .registerResourceInclude("META-INF/solon_def/.*\\.properties")
                    .registerResourceInclude(GraalvmUtil.SOLON_RESOURCE)
                    .registerResourceInclude(nativeMetadata.getNativeImageDir() + "/reflect-config.json");

            List<ResourceHint> includes = nativeMetadata.getIncludes();
            List<String> allResources = new ArrayList<>();
            for (ResourceHint include : includes) {
                for (String allowResource : ALLOW_RESOURCES) {
                    if (!include.getPattern().startsWith(allowResource)) {
                        continue;
                    }
                    Pattern pattern = Pattern.compile(include.getPattern());
                    Set<String> scanned = ScanUtil.scan(allowResource, path -> pattern.matcher(path).find());
                    if (!scanned.isEmpty()) {
                        allResources.addAll(scanned);
                    }
                }
            }

            FileWriter solonResourceFile = getFileWriter(nativeMetadata, GraalvmUtil.SOLON_RESOURCE_NAME);
            solonResourceFile.write(ONode.load(allResources, jsonOptions).toJson());
            solonResourceFile.close();

            FileWriter fileWriter = getFileWriter(nativeMetadata, "resource-config.json");
            fileWriter.write(nativeMetadata.toResourcesJson());
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 添加 reflect-config.json
     */
    private void addReflectConfig(RuntimeNativeMetadata nativeMetadata) {
        try {
            nativeMetadata.registerDefaultConstructor("org.noear.solon.extend.impl.PropsLoaderExt")
                    .registerDefaultConstructor("org.noear.solon.extend.impl.PropsConverterExt")
                    .registerDefaultConstructor("org.noear.solon.extend.impl.AppClassLoaderExt")
                    .registerDefaultConstructor("org.noear.solon.extend.impl.ReflectionExt")
                    .registerDefaultConstructor("org.noear.solon.extend.impl.ResourceScannerExt");

            FileWriter fileWriter = getFileWriter(nativeMetadata, "reflect-config.json");
            fileWriter.write(nativeMetadata.toReflectionJson());
            fileWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> getDefaultNativeImageArguments(String applicationClassName) {
        List<String> args = new ArrayList<>();
        args.add("-H:Class=" + applicationClassName);
        args.add("--report-unsupported-elements-at-runtime");
        args.add("--no-fallback");
        args.add("--install-exit-handlers");
        return args;
    }

    private FileWriter getFileWriter(RuntimeNativeMetadata nativeMetadata, String configName) {
        try {
            String dir = nativeMetadata.getNativeImageDir();
            String fileName = String.join("/", dir, configName);

            File file = new File(settings.classOutput + "/" + fileName);
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

    public static final class Settings {

        private Path classOutput;

        private String groupId;

        private String artifactId;

        public Settings(Path classOutput, String groupId, String artifactId){
            this.classOutput = classOutput;
            this.groupId = groupId;
            this.artifactId = artifactId;
        }

        public Path getClassOutput() {
            return classOutput;
        }

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }
    }

}
