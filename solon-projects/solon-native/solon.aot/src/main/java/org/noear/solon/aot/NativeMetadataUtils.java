package org.noear.solon.aot;

import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author songyinyin
 * @since 2023/10/22 20:33
 */
public class NativeMetadataUtils {

    /**
     * 将方法设置为可执行，同时注册方法参数、参数泛型和返回值、返回值泛型
     */
    public static void registerMethodAndParamAndReturnType(RuntimeNativeMetadata metadata, MethodWrap methodWrap) {
        metadata.registerMethod(methodWrap.getMethod(), ExecutableMode.INVOKE);

        ParamWrap[] paramWraps = methodWrap.getParamWraps();
        for (ParamWrap paramWrap : paramWraps) {
            Class<?> paramType = paramWrap.getType();
            metadata.registerReflection(paramType, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            if (!paramType.getName().startsWith("java.") && Serializable.class.isAssignableFrom(paramType)) {
                metadata.registerSerialization(paramType);
            }

            // 参数的泛型
            Type genericType = paramWrap.getGenericType();
            processGenericType(metadata, genericType);
        }

        Class<?> returnType = methodWrap.getReturnType();
        metadata.registerReflection(returnType, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
        if (!returnType.getName().startsWith("java.") && Serializable.class.isAssignableFrom(returnType)) {
            metadata.registerSerialization(returnType);
        }

        // 返回值的泛型
        Type genericReturnType = methodWrap.getGenericReturnType();
        processGenericType(metadata, genericReturnType);
    }

    private static void processGenericType(RuntimeNativeMetadata metadata, Type genericType) {
        Map<String, Type> genericInfo = GenericUtil.getGenericInfo(genericType);
        for (Map.Entry<String, Type> entry : genericInfo.entrySet()) {
            if (!entry.getValue().getTypeName().startsWith("java.")) {
                metadata.registerReflection(entry.getValue().getTypeName(), MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            }
        }
    }
}
