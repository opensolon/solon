/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.serialization.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Protostuff 序列化器
 *
 * @author noear
 * @since 2.8
 */
public class ProtostuffBytesSerializer implements ContextSerializer<byte[]> {
    private static final String label = "application/protobuf";
    private final Schema<DataWrapper> WRAPPER_SCHEMA = RuntimeSchema.createFrom(DataWrapper.class);

    @Override
    public String getContentType() {
        return label;
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
    }

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
    public Object deserialize(byte[] data, Type toType) throws IOException {
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
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(getContentType());
        ctx.output(serialize(data));
    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
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