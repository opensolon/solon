package org.noear.solon.aot;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.snack.core.Options;
import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.aot.hint.ResourceHint;
import org.noear.solon.aot.proxy.ProxyClassGenerator;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.runtime.NativeDetector;
import org.noear.solon.core.PluginEntity;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ScanUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Paths;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * aot 运行的启动类，用于生成 native 元数据
 *
 * @author songyinyin
 * @since 2023/4/11 14:11
 */
public class SolonAotProcessor {
    private BeanNativeProcessor beanNativeProcessor;

    private final ProxyClassGenerator proxyClassGenerator;

    private final Options jsonOptions = Options.def().add(Feature.PrettyFormat).add(Feature.OrderedField);

    private final Settings settings;

    private final String[] applicationArgs;

    private final Class<?> applicationClass;

    private static final List<String> ALLOW_RESOURCES = Arrays.asList("META-INF", "static", "templates", "sql");

    public SolonAotProcessor(Settings settings, String[] applicationArgs, Class<?> applicationClass) {
        this.settings = settings;
        this.applicationArgs = applicationArgs;
        this.applicationClass = applicationClass;
        this.proxyClassGenerator = new ProxyClassGenerator();
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

        //（静态扩展约定：org.noear.solon.extend.impl.XxxxExt）
        BeanNativeProcessor ext = ClassUtil.newInstance("org.noear.solon.extend.impl.AopContextNativeProcessorExt");
        if (ext != null) {
            beanNativeProcessor = ext;
        } else {
            beanNativeProcessor = new BeanNativeProcessorDefault();
        }

        AopContext context = Solon.app().context();

        RuntimeNativeMetadata metadata = new RuntimeNativeMetadata();
        metadata.setApplicationClassName(applicationClass.getCanonicalName());

        //处理 bean（生成配置、代理等...）
        processBean(context, metadata);

        List<PluginEntity> plugs = Solon.cfg().plugs();
        for (PluginEntity plug : plugs) {
            metadata.registerDefaultConstructor(plug.getClassName());
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

        Solon.stopBlock(false, -1);

        LogUtil.global().info("Aot processor end.");
    }

    /**
     * 处理 bean（生成配置、代理等...）
     * */
    private void processBean(AopContext context, RuntimeNativeMetadata metadata) {
        AtomicInteger beanCount = new AtomicInteger();
        context.beanForeach(beanWrap -> {
            // aot阶段产生的bean，不需要注册到 native 元数据里
            if (RuntimeNativeRegistrar.class.isAssignableFrom(beanWrap.clz())) {
                return;
            }

            Class<?> clz = beanWrap.clz();

            //如果是接口类型，则不处理（如果有需要手动处理）
            if (clz.isInterface()) {
                return;
            }

            //开始计数
            beanCount.getAndIncrement();

            //生成代理
            if (beanWrap.proxy() != null) {
                proxyClassGenerator.generateCode(settings, clz);
            }

            //注册信息（构造函数，初始化函数等...）
            if (beanWrap.clzInit() != null) {
                metadata.registerMethod(beanWrap.clzInit(), ExecutableMode.INVOKE);
            }

            beanNativeProcessor.processBean(metadata, clz, beanWrap.proxy() != null);
            beanNativeProcessor.processBeanFields(metadata, clz);
        });

        //for
        context.methodForeach(methodWrap -> {
            beanNativeProcessor.processMethod(metadata, methodWrap.getMethod());

            Class<?> returnType = methodWrap.getReturnType();
            if (returnType.getName().startsWith("java.") == false && Serializable.class.isAssignableFrom(returnType)) {
                //是 Serializable 的做自动注册（不则，怕注册太多了）
                metadata.registerSerialization(returnType);
            }

            Type genericReturnType = methodWrap.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {
                for (Type arg1 : ((ParameterizedType) genericReturnType).getActualTypeArguments()) {
                    if (arg1 instanceof Class) {
                        Class<?> arg1c = (Class<?>) arg1;
                        if (arg1c.getName().startsWith("java.") == false && Serializable.class.isAssignableFrom(arg1c)) {
                            //是 Serializable 的做自动注册（不则，怕注册太多了）
                            metadata.registerSerialization(arg1c);
                        }
                    }
                }
            }
        });

        //for @Inject(${..}) clz
        for (Class<?> clz : context.aot().getEntityTypes()) {
            if (clz.getName().startsWith("java.") == false) {
                metadata.registerReflection(clz, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS);
            }
        }

        //for jdk proxy interface
        for(Class<?> clz : context.aot().getJdkProxyTypes()) {
            if (clz.getName().startsWith("java.") == false) {
                metadata.registerJdkProxy(clz);
            }
        }

        //for sql Driver
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()){
            metadata.registerReflection(drivers.nextElement().getClass(), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        }


        LogUtil.global().info("Aot process bean, bean size: " + beanCount.get());
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

    private void addReflectConfigDo(RuntimeNativeMetadata metadata, String className){
        if(ClassUtil.loadClass(className) != null){
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
