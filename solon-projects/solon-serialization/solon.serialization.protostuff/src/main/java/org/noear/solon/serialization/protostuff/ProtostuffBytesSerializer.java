package org.noear.solon.serialization.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * @author noear
 * @since 2.8
 */
public class ProtostuffBytesSerializer implements ActionSerializer<byte[]> {
    private final Schema<DataWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(DataWrapper.class);

    @Override
    public String name() {
        return "protostuff-bytes";
    }

    @Override
    public byte[] serialize(Object obj) throws IOException {
        LinkedBuffer buffer = LinkedBuffer.allocate();

        try {
            Object serializerObj = DataWrapper.builder(obj);
            Schema schema = WRAPPER_SCHEMA;

            return ProtostuffIOUtil.toByteArray(serializerObj, schema, buffer);
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public Object deserialize(byte[] data, Class<?> clz) throws IOException {
        try {
            DataWrapper wrapper = new DataWrapper();
            ProtostuffIOUtil.mergeFrom(data, wrapper, WRAPPER_SCHEMA);
            return wrapper.getData();
        } catch (RuntimeException e) {
            throw e;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        return deserialize(ctx.bodyAsBytes(), null);
    }

    //静态内部类
    public static class DataWrapper<T> {
        //泛型的使用
        private T data;

        //建造者模式(返回实体类型)
        public static <T> DataWrapper<T> builder(T data) {
            DataWrapper<T> wrapper = new DataWrapper<T>();
            wrapper.setData(data);
            return wrapper;
        }

        public void setData(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }
    }
}