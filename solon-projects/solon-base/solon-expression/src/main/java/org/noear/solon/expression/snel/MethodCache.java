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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 方法缓存
 *
 * @author noear
 * @since 3.1
 * */
public class MethodCache {
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

    private final Map<MethodKey, Method> cache = new HashMap<>();

    public Method getMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        MethodKey key = new MethodKey(clazz, methodName, argTypes);
        return cache.computeIfAbsent(key, k -> findMethod(clazz, methodName, argTypes));
    }

    private Method findMethod(Class<?> clazz, String methodName, Class<?>[] argTypes) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class<?>[] paramTypes = method.getParameterTypes();
                if (paramTypes.length == argTypes.length) {
                    boolean match = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        if (!isAssignable(paramTypes[i], argTypes[i])) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        return method;
                    }
                }
            }
        }
        return null;
    }

    private boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
        if (targetType.isPrimitive()) {
            Class<?> wrapperType = PRIMITIVE_WRAPPER_MAP.get(targetType);
            return wrapperType != null && wrapperType.isAssignableFrom(sourceType);
        }

        if (targetType.isAssignableFrom(sourceType)) {
            return true;
        }

        if (sourceType == Void.class) {//表示 source-val 为 null
            return true;
        }

        return false;
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