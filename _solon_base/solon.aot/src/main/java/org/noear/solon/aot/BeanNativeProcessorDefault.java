package org.noear.solon.aot;

import org.noear.solon.aot.hint.ExecutableHint;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * 收集运行时的类、字段、方法，用于bean扫描时使用，本类会对所有bean进行处理
 *
 * @author songyinyin
 * @see org.noear.solon.aot.graalvm.GraalvmUtil
 * @since 2.2
 */
public class BeanNativeProcessorDefault implements BeanNativeProcessor {
    public static final String AOT_PROXY_CLASSNAME_SUFFIX ="$$SolonAotProxy";
    public static final String ASM_PROXY_CLASSNAME_SUFFIX ="$$SolonAsmProxy";

    @Override
    public void processBean(RuntimeNativeMetadata nativeMetadata, Class<?> clazz, boolean supportProxy) {
        if (clazz.isEnum() || clazz.isAnnotation()) {
            return;
        }
        nativeMetadata.registerDefaultConstructor(clazz);
        for (Field field : ReflectUtil.getDeclaredFields(clazz)) {
            nativeMetadata.registerField(field);
        }
        nativeMetadata.registerReflection(clazz, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        // 处理代理类（代理类构造时会用到反射）
        if (supportProxy) {
            String proxyClassName = ReflectUtil.getClassName(clazz) + AOT_PROXY_CLASSNAME_SUFFIX;

            nativeMetadata.registerReflection(proxyClassName, hints ->
                    hints.getConstructors().add(new ExecutableHint("<init>", new Class[]{InvocationHandler.class}, ExecutableMode.INVOKE)));
            nativeMetadata.registerReflection(proxyClassName, hints ->
                    hints.getMemberCategories().addAll((Arrays.asList(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS))));

        }
    }

    @Override
    public void processMethod(RuntimeNativeMetadata nativeMetadata, Method method) {
        nativeMetadata.registerMethod(method, ExecutableMode.INVOKE);
    }

    @Override
    public void processField(RuntimeNativeMetadata nativeMetadata, Field field) {
        nativeMetadata.registerField(field);
        nativeMetadata.registerReflection(field.getDeclaringClass(), MemberCategory.DECLARED_FIELDS);
    }
}
