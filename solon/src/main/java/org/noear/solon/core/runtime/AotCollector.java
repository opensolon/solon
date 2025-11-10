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
package org.noear.solon.core.runtime;

import org.noear.eggg.MethodEggg;
import org.noear.solon.lang.Internal;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Aot 收集器
 *
 * @author noear
 * @since  2.2
 */
@Internal
public class AotCollector {
    private final Set<Class<?>> entityTypes = new LinkedHashSet<>();
    private final Set<Class<?>> jdkProxyTypes = new LinkedHashSet<>();
    private final Set<MethodEggg> methodEgggs = new LinkedHashSet<>();

    /**
     * 获取实体类型
     */
    public Set<Class<?>> getEntityTypes() {
        return entityTypes;
    }

    /**
     * 获取Jdk代理类型
     */
    public Set<Class<?>> getJdkProxyTypes() {
        return jdkProxyTypes;
    }

    public Set<MethodEggg> getMethodEgggs() {
        return methodEgggs;
    }

    /**
     * 注册实体类型
     */
    public void registerEntityType(Class<?> type, Type genericType) {
        if (NativeDetector.isAotRuntime()) {
            if (type.getName().startsWith("java.") == false) {
                entityTypes.add(type);
            }

            if (genericType instanceof ParameterizedType) {
                ParameterizedType genericType1 = (ParameterizedType) genericType;
                for (Type type1 : genericType1.getActualTypeArguments()) {
                    if (type1 instanceof Class) {
                        if (type1.getTypeName().startsWith("java.") == false) {
                            entityTypes.add((Class<?>) type1);
                        }
                    }
                }
            }
        }
    }

    /**
     * 注册jdk代理类型
     */
    public void registerJdkProxyType(Class<?> type, Object target) {
        if (NativeDetector.isAotRuntime()) {
            if (Proxy.isProxyClass(target.getClass())) {
                if (type.getName().startsWith("java.") == false) {
                    jdkProxyTypes.add(type);
                }
            }
        }
    }

    public void registerMethodEggg(MethodEggg methodEggg) {
        methodEgggs.add(methodEggg);
    }

    /**
     * 清空
     */
    public void clear() {
        entityTypes.clear();
        jdkProxyTypes.clear();
        methodEgggs.clear();
    }
}
