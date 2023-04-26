package org.noear.solon.aot;

import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.core.util.ReflectUtil;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * 收集运行时的类、字段、方法，用于bean扫描时使用，本类会对所有bean进行处理
 *
 * @author songyinyin
 * @see org.noear.solon.aot.graalvm.GraalvmUtil
 * @since 2.2
 */
public class DefaultAopContextNativeProcessor implements AopContextNativeProcessor {
    public static final String AOT_PROXY_CLASSNAME_SUFFIX ="$$SolonAotProxy";
    public static final String ASM_PROXY_CLASSNAME_SUFFIX ="$$SolonAsmProxy";

    @Override
    public void processBean(RuntimeNativeMetadata nativeMetadata, BeanWrap beanWrap) {
        if (beanWrap.clz().isEnum() || beanWrap.clz().isAnnotation()) {
            return;
        }
        nativeMetadata.registerDefaultConstructor(beanWrap.clz());
        for (Field field : ReflectUtil.getDeclaredFields(beanWrap.clz())) {
            nativeMetadata.registerField(field);
        }
        nativeMetadata.registerReflection(beanWrap.clz(), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);

        // 处理代理类（代理类构造时会用到反射）
        Class<?> proxyClass = ClassUtil.loadClass(ReflectUtil.getClassName(beanWrap.clz()) + AOT_PROXY_CLASSNAME_SUFFIX);
        if (proxyClass != null) {
            Constructor<?>[] declaredConstructors = proxyClass.getDeclaredConstructors();
            for (Constructor<?> declaredConstructor : declaredConstructors) {
                nativeMetadata.registerConstructor(declaredConstructor, ExecutableMode.INVOKE);
            }
            nativeMetadata.registerReflection(proxyClass, MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
        }
    }

    @Override
    public void processMethod(RuntimeNativeMetadata nativeMetadata, MethodWrap methodWrap) {
        nativeMetadata.registerMethod(methodWrap.getMethod(), ExecutableMode.INVOKE);
    }

    @Override
    public void processField(RuntimeNativeMetadata nativeMetadata, FieldWrap fieldWrap) {
        nativeMetadata.registerField(fieldWrap.field);
        nativeMetadata.registerReflection(fieldWrap.field.getDeclaringClass(), MemberCategory.DECLARED_FIELDS);
    }
}
