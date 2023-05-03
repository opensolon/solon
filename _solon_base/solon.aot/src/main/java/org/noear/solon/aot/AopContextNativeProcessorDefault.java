package org.noear.solon.aot;

import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.aot.proxy.ProxyClassGenerator;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.core.util.ReflectUtil;
import org.noear.solon.core.wrap.ClassWrap;
import org.noear.solon.core.wrap.FieldWrap;

import java.io.Serializable;
import java.lang.reflect.*;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 收集运行时的类、字段、方法，用于bean扫描时使用，本类会对 AopContext 进行整体处理
 *
 * @author songyinyin
 * @see org.noear.solon.aot.graalvm.GraalvmUtil
 * @since 2.2
 */
public class AopContextNativeProcessorDefault implements AopContextNativeProcessor {
    public static final String AOT_PROXY_CLASSNAME_SUFFIX ="$$SolonAotProxy";
    public static final String ASM_PROXY_CLASSNAME_SUFFIX ="$$SolonAsmProxy";

    private final ProxyClassGenerator proxyClassGenerator;

    public AopContextNativeProcessorDefault(){
        proxyClassGenerator = new ProxyClassGenerator();
    }


    /**
     * 处理（生成配置、代理等...）
     *
     * @param context  上下文
     * @param settings 运行设置
     * @param metadata 元信息对象
     */
    public void process(AopContext context, Settings settings, RuntimeNativeMetadata metadata) {
        AtomicInteger beanCount = new AtomicInteger();

        //for beanWrap
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

            processBeanDo(metadata, clz, beanWrap.proxy() != null);
            processBeanFieldsDo(metadata, clz);
        });

        //for methodWrap
        context.methodForeach(methodWrap -> {
            processMethodDo(metadata, methodWrap.getMethod());

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

        //for @Inject(${..}) clz (entity)
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

        //for jdbc Driver
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()){
            metadata.registerReflection(drivers.nextElement().getClass(), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        }

        //for InvocationHandler
        metadata.registerReflection(InvocationHandler.class, MemberCategory.INVOKE_DECLARED_METHODS);


        LogUtil.global().info("Aot process bean, bean size: " + beanCount.get());
    }



    protected void processBeanDo(RuntimeNativeMetadata nativeMetadata, Class<?> clazz, boolean supportProxy) {
        if (clazz.isEnum() || clazz.isAnnotation()) {
            return;
        }

        // 注册
        nativeMetadata.registerDefaultConstructor(clazz);
        for (Field field : ReflectUtil.getDeclaredFields(clazz)) {
            nativeMetadata.registerField(field);
        }
        nativeMetadata.registerReflection(clazz, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        // 注册代理类（代理类构造时会用到反射）
        if (supportProxy) {
            String proxyClassName = ReflectUtil.getClassName(clazz) + AOT_PROXY_CLASSNAME_SUFFIX;

            nativeMetadata.registerReflection(proxyClassName, hints ->
                    hints.getConstructors().add(new ExecutableHint("<init>", new Class[]{InvocationHandler.class}, ExecutableMode.INVOKE)));
            nativeMetadata.registerReflection(proxyClassName, hints ->
                    hints.getMemberCategories().addAll((Arrays.asList(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))));

        }
    }


    protected void processMethodDo(RuntimeNativeMetadata nativeMetadata, Method method) {
        nativeMetadata.registerMethod(method, ExecutableMode.INVOKE);
    }

    protected void processBeanFieldsDo(RuntimeNativeMetadata nativeMetadata, Class<?> clazz){
        ClassWrap clzWrap = ClassWrap.get(clazz);

        // 处理字段
        Map<String, FieldWrap> fieldAllWraps = clzWrap.getFieldAllWraps();
        for (FieldWrap fieldWrap : fieldAllWraps.values()) {
            processFieldDo(nativeMetadata, fieldWrap.field);
        }
    }

    protected void processFieldDo(RuntimeNativeMetadata nativeMetadata, Field field) {
        nativeMetadata.registerField(field);
        nativeMetadata.registerReflection(field.getDeclaringClass(), MemberCategory.DECLARED_FIELDS);
    }
}
