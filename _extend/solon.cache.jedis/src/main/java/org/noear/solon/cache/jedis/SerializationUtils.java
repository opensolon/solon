package org.noear.solon.cache.jedis;

import java.io.*;

/**
 * @author noear
 * @since 1.3
 */
public class SerializationUtils {
    /**
     * 序列化
     */
    public static byte[] serialize(Object object) {
        if (object == null) {
            return null;
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

            try {
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(object);
                oos.flush();
            } catch (IOException var3) {
                throw new IllegalArgumentException("Failed to serialize object of type: " + object.getClass(), var3);
            }

            return baos.toByteArray();
        }
    }

    /**
     * 反序列化
     */
    public static Object deserialize(byte[] bytes) {
        if (bytes == null) {
            return null;
        } else {
            try {
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
                return ois.readObject();
            } catch (IOException var2) {
                throw new IllegalArgumentException("Failed to deserialize object", var2);
            } catch (ClassNotFoundException var3) {
                throw new IllegalStateException("Failed to deserialize object type", var3);
            }
        }
    }
}
