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
package org.noear.solon.data.cache.impl;

import org.noear.solon.core.AppClassLoader;

import java.io.*;

/**
 * ObjectInputStream 增加类加载控制
 *
 * @author noear
 * @since 2.8
 */
public class ObjectInputStreamEx extends ObjectInputStream {
    private ClassLoader loader;

    public ObjectInputStreamEx(ClassLoader loader, InputStream in) throws IOException {
        super(in);

        if (loader == null) {
            this.loader = AppClassLoader.global();
        } else {
            this.loader = loader;
        }
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        String name = desc.getName();
        try {
            return Class.forName(name, false, loader);
        } catch (ClassNotFoundException ex) {
            throw ex;
        }
    }
}