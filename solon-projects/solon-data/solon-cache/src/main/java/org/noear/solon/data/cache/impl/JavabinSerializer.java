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

import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Javabin 序列化实现
 *
 * @author noear
 * @since 2.5
 */
public class JavabinSerializer implements Serializer<String> {
    public static final JavabinSerializer instance = new JavabinSerializer();

    @Override
    public String name() {
        return "java-bin";
    }

    @Override
    public String serialize(Object obj) {
        if (obj == null) {
            return null;
        }

        byte[] tmp = serializeDo(obj);
        return Base64.getEncoder().encodeToString(tmp);
    }

    @Override
    public Object deserialize(String dta, Type toType) {
        if (dta == null) {
            return null;
        }

        //分析类加载器
        ClassLoader loader = ClassUtil.resolveClassLoader(toType);

        byte[] bytes = Base64.getDecoder().decode(dta);
        return deserializeDo(loader, bytes);
    }


    protected byte[] serializeDo(Object object) {
        if (object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), e);
            }

            return baos.toByteArray();
        }
    }

    /**
     * 反序列化
     */
    protected Object deserializeDo(ClassLoader loader, byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStreamEx(loader, new ByteArrayInputStream(bytes));
                return ois.readObject();
            } catch (IOException e) {
                throw new IllegalArgumentException("Failed to deserialize object", e);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Failed to deserialize object type", e);
            }
        }
    }
}