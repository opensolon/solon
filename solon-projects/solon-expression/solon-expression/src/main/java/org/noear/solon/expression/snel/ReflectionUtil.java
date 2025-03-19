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
package org.noear.solon.expression.snel;

import org.noear.solon.expression.exception.EvaluationException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 反射工具
 *
 * @author noear
 * @since 3.1
 * */
public class ReflectionUtil {
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPER_MAP = new HashMap<>();

    static {
        PRIMITIVE_WRAPPER_MAP.put(byte.class, Byte.class);
        PRIMITIVE_WRAPPER_MAP.put(short.class, Short.class);
        PRIMITIVE_WRAPPER_MAP.put(int.class, Integer.class);
        PRIMITIVE_WRAPPER_MAP.put(long.class, Long.class);
        PRIMITIVE_WRAPPER_MAP.put(float.class, Float.class);
        PRIMITIVE_WRAPPER_MAP.put(double.class, Double.class);
        PRIMITIVE_WRAPPER_MAP.put(boolean.class, Boolean.class);
        // 可以添加更多的基本类型和包装类型映射
    }

    private final Map<MethodKey, Method> cache = new ConcurrentHashMap<>();
    private final Map<Class<?>, Method[]> methodsCache = new ConcurrentHashMap<>();

    private Method[] getMethods(Class<?> clazz) {
        return methodsCache.computeIfAbsent(clazz, Class::getMethods);
    }

    public Method getMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        MethodKey key = new MethodKey(clazz, methodName, argTypes);
        return cache.computeIfAbsent(key, k -> findMethod(clazz, methodName, argTypes));
    }


    // 优化参数类型匹配逻辑
    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        // 使用流处理并并行查找（如果线程安全）
        Method method = Arrays.stream(getMethods(clazz))
                .filter(m -> m.getName().equals(methodName))
                .filter(m -> isMethodMatch(m, argTypes))
                .findFirst()
                .orElse(null);

        if (method != null) {
            method.setAccessible(true);
        }

        return method;
    }

    private boolean isMethodMatch(Method method, Class<?>[] argTypes) {
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length != argTypes.length) return false;

        for (int i = 0; i < paramTypes.length; i++) {
            if (!isAssignable(paramTypes[i], argTypes[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        // 处理原始类型与包装类型的兼容性
        if (targetType.isPrimitive()) {
            Class<?> wrapperType = PRIMITIVE_WRAPPER_MAP.get(targetType);
            return wrapperType != null && wrapperType.isAssignableFrom(sourceType);
        } else if (sourceType.isPrimitive()) {
            Class<?> targetWrapper = PRIMITIVE_WRAPPER_MAP.get(sourceType);
            return targetType.isAssignableFrom(targetWrapper);
        }

        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        if (sourceType == Void.class) {//表示 source-val 为 null
            return true;
        }

        return false;
    }


    /// //////////////////////////////
    private static final Map<String, PropertyHolder> PROPERTY_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取属性
     */
    public static PropertyHolder getProperty(Class<?> clazz, String propName) {
        String key = clazz.getName() + ":" + propName;

        return PROPERTY_CACHE.computeIfAbsent(key, k -> {
            try {
                String name = "get" + capitalize(propName);
                Method method = clazz.getMethod(name);
                method.setAccessible(true);

                return new PropertyHolder(method, null);
            } catch (NoSuchMethodException e) {
                try {
                    Field field = clazz.getField(propName);
                    field.setAccessible(true);

                    return new PropertyHolder(null, field);
                } catch (NoSuchFieldException ex) {
                    throw new EvaluationException("Missing property: " + propName, e);
                }
            }
        });
    }

    /**
     * 将字符串首字母大写
     */
    private static String capitalize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    private static class MethodKey {
        private final Class<?> clazz;
        private final String methodName;
        private final Class<?>[] argTypes;

        public MethodKey(Class<?> clazz, String methodName, Class<?>[] argTypes) {
            this.clazz = clazz;
            this.methodName = methodName;
            this.argTypes = argTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodKey methodKey = (MethodKey) o;
            return clazz.equals(methodKey.clazz) &&
                    methodName.equals(methodKey.methodName) &&
                    java.util.Arrays.equals(argTypes, methodKey.argTypes);
        }

        @Override
        public int hashCode() {
            int result = clazz.hashCode();
            result = 31 * result + methodName.hashCode();
            result = 31 * result + java.util.Arrays.hashCode(argTypes);
            return result;
        }
    }
}