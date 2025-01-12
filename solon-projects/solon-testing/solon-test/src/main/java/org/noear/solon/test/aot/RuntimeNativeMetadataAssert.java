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
package org.noear.solon.test.aot;

import org.noear.solon.Solon;
import org.noear.solon.aot.RuntimeNativeMetadata;
import org.noear.solon.aot.graalvm.GraalvmUtil;
import org.noear.solon.aot.hint.JdkProxyHint;
import org.noear.solon.aot.hint.SerializationHint;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.util.ReflectUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用来断言 生成的 graalvm native 可达性元数据是否正确
 *
 * @author songyinyin
 * @since 2023/10/23 17:54
 */
public abstract class RuntimeNativeMetadataAssert {

    private static NativeMetadataReflectAssert REFLECT_ASSERT;

    public static NativeMetadataReflectAssert reflect() {
        if (REFLECT_ASSERT == null) {
            REFLECT_ASSERT = new NativeMetadataReflectAssert();
        }
        return REFLECT_ASSERT;
    }

    /**
     * 断言 native metadata 中是否注册了此类的序列化
     *
     * @param type 序列化的类
     */
    public static void hasSerializationType(Class<?> type) {
        SerializationHint serializationHint = getMetadata().getSerializations().get(ReflectUtil.getClassName(type));
        notNull(serializationHint, String.format("serialization type '%s' not found in native metadata", type.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此类的序列化
     *
     * @param className 序列化的类名
     */
    public static void hasSerializationType(String className) {
        SerializationHint serializationHint = getMetadata().getSerializations().get(className);
        notNull(serializationHint, String.format("serialization type '%s' not found in native metadata", className));
    }

    /**
     * 断言 native metadata 中是否注册了此类的 lambda 序列化
     *
     * @param type 序列化的类
     */
    public static void hasLambdaSerializationType(Class<?> type) {
        SerializationHint serializationHint = getMetadata().getLambdaSerializations().get(ReflectUtil.getClassName(type));
        notNull(serializationHint, String.format("lambda serialization type '%s' not found in native metadata", type.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此类的 lambda 序列化
     *
     * @param className 序列化的类名
     */
    public static void hasLambdaSerializationType(String className) {
        SerializationHint serializationHint = getMetadata().getLambdaSerializations().get(className);
        notNull(serializationHint, String.format("lambda serialization type '%s' not found in native metadata", className));
    }

    /**
     * 断言 native metadata 中是否注册了此类的 jdk 代理
     *
     * @param type 代理的类
     */
    public static void hasJdkProxyType(Class<?> type) {
        JdkProxyHint jdkProxyHint = getMetadata().getJdkProxys().get(ReflectUtil.getClassName(type));
        notNull(jdkProxyHint, String.format("jdk proxy type '%s' not found in native metadata", type.getName()));
    }

    /**
     * 断言 native metadata 中是否注册了此类的 jdk 代理
     *
     * @param className 代理的类名
     */
    public static void hasJdkProxyType(String className) {
        JdkProxyHint jdkProxyHint = getMetadata().getJdkProxys().get(className);
        notNull(jdkProxyHint, String.format("jdk proxy type '%s' not found in native metadata", className));
    }

    /**
     * 断言 native metadata 中是否注册了此 文件资源
     *
     * @param resource 文件资源
     */
    public static void resourceIsInclude(String resource) {
        Set<String> resources = GraalvmUtil.getResources();
        assertTrue(resources.contains(resource), String.format("resource '%s' not found in native metadata", resource));
    }


    protected static RuntimeNativeMetadata getMetadata() {
        AppContext context = Solon.context();
        if (context == null) {
            throw new IllegalArgumentException("solon app not initialized");
        }
        return context.getBean(RuntimeNativeMetadata.class);
    }

    protected static Method getMethod(Class<?> type, String methodName) {
        List<Method> methods = Arrays.stream(ReflectUtil.getDeclaredMethods(type))
                .filter(method -> method.getName().equals(methodName))
                .collect(Collectors.toList());
        if (methods.size() == 1) {
            return methods.iterator().next();
        } else if (methods.size() > 1) {
            throw new IllegalArgumentException(String.format("Found multiple methods named '%s' on class %s", methodName, type.getName()));
        } else {
            throw new IllegalArgumentException(String.format("No method named '%s' on class %s", methodName, type.getName()));
        }
    }

    protected static Field findField(Class<?> clazz, String name) {
        Class<?> searchType = clazz;
        while (Object.class != searchType && searchType != null) {
            Field[] fields = ReflectUtil.getDeclaredFields(searchType);
            for (Field field : fields) {
                if (name.equals(field.getName())) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    protected static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    protected static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notEmpty(Object[] array, String message) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException(message);
        }
    }

    protected static void hasText(String text, String message) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

}
