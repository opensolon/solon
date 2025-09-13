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
package org.noear.solon.serialization.protostuff;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.lang.Nullable;
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
    private static final ProtostuffBytesSerializer _default = new ProtostuffBytesSerializer();

    public static ProtostuffBytesSerializer getDefault() {
        return _default;
    }

    /**
     * @deprecated 3.6 {@link #getDefault()}
     * */
    @Deprecated
    public static ProtostuffBytesSerializer getInstance() {
        return _default;
    }

    /**
     * 获取内容类型
     */
    @Override
    public String mimeType() {
        return label;
    }

    /**
     * 数据类型
     */
    @Override
    public Class<byte[]> dataType() {
        return byte[].class;
    }

    @Override
    public boolean bodyRequired() {
        return true;
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "protostuff-bytes";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public byte[] serialize(Object obj) throws IOException {
        if (obj == null) {
            return new byte[0];
        }

        LinkedBuffer buffer = LinkedBuffer.allocate();

        try {
            Schema schema = RuntimeSchema.getSchema(obj.getClass());

            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(byte[] data, Type toType) throws IOException {
        if (toType instanceof Class<?>) {
            Schema schema = RuntimeSchema.getSchema((Class<? extends Object>) toType);
            Object tmp = schema.newMessage();
            ProtostuffIOUtil.mergeFrom(data, tmp, schema);
            return tmp;
        } else {
            throw new IllegalStateException("The parameter 'toType' is not Class");
        }
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.startsWith(label);
        }
    }

    /**
     * 序列化主体
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(this.mimeType());
        }

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    /**
     * 反序列化主体
     *
     * @param ctx 请求上下文
     */
    @Override
    public Object deserializeFromBody(Context ctx, @Nullable Type bodyType) throws IOException {
        return deserialize(ctx.bodyAsBytes(), bodyType);
    }
}