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

import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Javabin 序列化实现。
 */
public class JavabinSerializer implements Serializer<String> {

    private static final JavabinSerializer instance = new JavabinSerializer();

    public static JavabinSerializer getInstance() {
        return instance;
    }

    private final JavabinClassFilter classFilter;

    public JavabinSerializer() {
        this(JavabinClassFilter.defaults());
    }

    public JavabinSerializer(JavabinClassFilter classFilter) {
        this.classFilter = (classFilter != null ? classFilter : JavabinClassFilter.defaults());
    }

    public JavabinClassFilter classFilter() {
        return classFilter;
    }

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
    public Object deserialize(String dta, Type toType) throws IOException {
        if (dta == null) {
            return null;
        }

        ClassLoader loader = ClassUtil.resolveClassLoader(toType);

        byte[] bytes = Base64.getDecoder().decode(dta);
        return deserializeDo(loader, bytes);
    }

    protected byte[] serializeDo(Object object) {
        if (object == null) {
            return null;
        }

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

    protected Object deserializeDo(ClassLoader loader, byte[] bytes) throws IOException {
        if (bytes == null) {
            return null;
        }
        try {
            ObjectInputStream ois = new SafeObjectInputStream(loader, classFilter, new ByteArrayInputStream(bytes));
            return ois.readObject();
        } catch (InvalidClassException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize object type", e);
        }
    }
}
