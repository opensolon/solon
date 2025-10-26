/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.aot;

import org.noear.eggg.MethodEggg;
import org.noear.eggg.ParamEggg;
import org.noear.eggg.TypeEggg;
import org.noear.solon.aot.hint.ExecutableMode;
import org.noear.solon.aot.hint.MemberCategory;
import org.noear.solon.core.util.EgggUtil;
import org.noear.solon.core.util.GenericUtil;
import org.noear.solon.core.wrap.MethodWrap;

import java.io.Serializable;
import java.lang.reflect.Method;
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
    public static void registerMethodAndParamAndReturnType(RuntimeNativeMetadata metadata, Method method, Class<?> implementationClass) {
        MethodEggg methodEggg = EgggUtil.getClassEggg(implementationClass).findMethodEgggOrNew(method);
        registerMethodAndParamAndReturnType(metadata, methodEggg);
    }

    /**
     * 将方法设置为可执行，同时注册方法参数、参数泛型和返回值、返回值泛型
     */
    public static void registerMethodAndParamAndReturnType(RuntimeNativeMetadata metadata, MethodWrap methodWrap) {
        registerMethodAndParamAndReturnType(metadata, methodWrap.getMethodEggg());
    }

    /**
     * 将方法设置为可执行，同时注册方法参数、参数泛型和返回值、返回值泛型
     */
    public static void registerMethodAndParamAndReturnType(RuntimeNativeMetadata metadata, MethodEggg methodEggg) {
        try {
            metadata.registerMethod(methodEggg.getMethod(), ExecutableMode.INVOKE);

            for (ParamEggg paramEggg : methodEggg.getParamEgggAry()) {
                Class<?> paramType = paramEggg.getType();
                metadata.registerReflection(paramType, MemberCategory.PUBLIC_FIELDS, MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
                if (!paramType.getName().startsWith("java.") && Serializable.class.isAssignableFrom(paramType)) {
                    metadata.registerSerialization(paramType);
                }

                // 参数的泛型
                Type genericType = paramEggg.getGenericType();
                processGenericType(metadata, genericType);
            }

            TypeEggg returnTypeEggg = methodEggg.getReturnTypeEggg();

            metadata.registerReflection(returnTypeEggg.getType(), MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            if (!returnTypeEggg.getType().getName().startsWith("java.") && Serializable.class.isAssignableFrom(returnTypeEggg.getType())) {
                metadata.registerSerialization(returnTypeEggg.getType());
            }

            // 返回值的泛型
            Type genericReturnType = returnTypeEggg.getGenericType();
            processGenericType(metadata, genericReturnType);
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Can't register method: " + methodEggg.getMethod(), ex);
        }
    }

    private static void processGenericType(RuntimeNativeMetadata metadata, Type genericType) {
        if (genericType == null) {
            return;
        }

        Map<String, Type> genericInfo = GenericUtil.getGenericInfo(genericType);
        for (Map.Entry<String, Type> entry : genericInfo.entrySet()) {
            if (!entry.getValue().getTypeName().startsWith("java.")) {
                metadata.registerReflection(entry.getValue().getTypeName(), MemberCategory.DECLARED_FIELDS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS);
            }
        }
    }
}