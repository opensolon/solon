package org.noear.nami.coder.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protostuff 序列化工具
 *
 * @author noear
 * @since 1.2
 * @since 2.4
 */
public class ProtostuffUtil {
    //序列化对象
    public static <T> byte[] serialize(T obj) {
        LinkedBuffer buffer = null;

        try {
            buffer = LinkedBuffer.allocate();

            Schema schema = RuntimeSchema.getSchema(obj.getClass());

            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            if (buffer != null) {
                buffer.clear();
            }
        }
    }

    public static <T> T deserialize(byte[] data, Class<T> targetType) {
        if (targetType == Void.class) {
            return null;
        }

        try {
            Schema schema = RuntimeSchema.getSchema(targetType);
            Object obj = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, obj, schema);
            return (T) obj;
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
