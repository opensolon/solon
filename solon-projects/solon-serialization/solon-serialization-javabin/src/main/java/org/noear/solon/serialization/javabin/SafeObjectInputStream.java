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
package org.noear.solon.serialization.javabin;

import org.noear.solon.core.AppClassLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.lang.reflect.Proxy;

/**
 * 带类名白名单过滤的 ObjectInputStream。
 *
 * <p>同时覆盖 {@link #resolveClass(ObjectStreamClass)} 和
 * {@link #resolveProxyClass(String[])}。
 */
public class SafeObjectInputStream extends ObjectInputStream {
    private final ClassLoader loader;
    private final JavabinClassFilter classFilter;

    public SafeObjectInputStream(ClassLoader loader, JavabinClassFilter classFilter, InputStream in) throws IOException {
        super(in);
        if (loader == null) {
            this.loader = AppClassLoader.global();
        } else {
            this.loader = loader;
        }
        if (classFilter == null) {
            this.classFilter = new JavabinClassFilter();
        } else {
            this.classFilter = classFilter;
        }
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        if (!classFilter.isAllowed(name)) {
            throw new InvalidClassException(name,
                    "class is not allowed by JavabinClassFilter; "
                            + "if it is safe to deserialize, add an allow pattern "
                            + "(e.g. JavabinSerializer#classFilter().allow(\"com.yourapp.\"))");
        }
        return Class.forName(name, false, loader);
    }

    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) throws IOException, ClassNotFoundException {
        if (interfaces == null || interfaces.length == 0) {
            throw new InvalidClassException("proxy",
                    "dynamic proxy with empty interface list is not allowed");
        }
        Class<?>[] interfaceClasses = new Class<?>[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            String name = interfaces[i];
            if (!classFilter.isAllowed(name)) {
                throw new InvalidClassException(name,
                        "proxy interface is not allowed by JavabinClassFilter");
            }
            interfaceClasses[i] = Class.forName(name, false, loader);
        }
        try {
            return Proxy.getProxyClass(loader, interfaceClasses);
        } catch (IllegalArgumentException e) {
            throw new ClassNotFoundException("cannot create proxy class", e);
        }
    }
}
