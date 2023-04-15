package org.noear.solon.aot;

import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.wrap.FieldWrap;
import org.noear.solon.core.wrap.MethodWrap;

import java.lang.reflect.Field;

/**
 * 收集运行时的类、字段、方法，用于bean扫描时使用，本类会对所有bean进行处理
 *
 * @author songyinyin
 * @see org.noear.solon.aot.graalvm.GraalvmUtil
 * @since 2023/4/12 17:49
 */
public class DefaultAopContextNativeProcessor implements AopContextNativeProcessor {

    @Override
    public void processBean(RuntimeNativeMetadata nativeMetadata, BeanWrap beanWrap) {
        if (beanWrap.clz().isInterface() || beanWrap.clz().isEnum() || beanWrap.clz().isAnnotation()) {
            return;
        }
        nativeMetadata.registerDefaultConstructor(beanWrap.clz());
        for (Field field : beanWrap.clz().getDeclaredFields()) {
            nativeMetadata.registerField(field);
        }
        nativeMetadata.registerReflection(beanWrap.clz(), MemberCategory.INVOKE_DECLARED_CONSTRUCTORS);
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
