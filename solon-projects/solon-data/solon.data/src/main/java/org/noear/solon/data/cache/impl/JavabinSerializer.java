package org.noear.solon.data.cache.impl;

import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.io.*;
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
    public Object deserialize(String dta, Class<?> clz) {
        if (dta == null) {
            return null;
        }

        //分析类加载器
        ClassLoader loader = ClassUtil.resolveClassLoader(clz);

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