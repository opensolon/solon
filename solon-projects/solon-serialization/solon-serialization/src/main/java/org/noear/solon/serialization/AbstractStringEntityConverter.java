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
package org.noear.solon.serialization;

import org.noear.solon.core.handle.AbstractEntityConverter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.util.Assert;

/**
 * 虚拟字符串实体转换器
 *
 * @author noear
 * @since 3.6
 */
public abstract class AbstractStringEntityConverter<T extends EntitySerializer<String>> extends AbstractEntityConverter {
    protected final T serializer;

    public T getSerializer() {
        return serializer;
    }

    public AbstractStringEntityConverter(T serializer) {
        Assert.notNull(serializer, "Serializer not be null");
        this.serializer = serializer;
    }

    protected boolean isWriteType() {
        return false;
    }

    @Override
    public boolean canWrite(String mime, Context ctx) {
        return serializer.matched(ctx, mime);
    }

    @Override
    public String writeAndReturn(Object data, Context ctx) throws Throwable {
        return serializer.serialize(data);
    }

    @Override
    public void write(Object data, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", name());
        }

        String text = null;

        if (isWriteType()) {
            //序列化处理
            //
            text = serializer.serialize(data);
        } else {
            //非序列化处理
            //
            if (data == null) {
                return;
            }

            if (data instanceof Throwable) {
                throw (Throwable) data;
            }

            if (data instanceof String) {
                text = (String) data;
            } else {
                text = serializer.serialize(data);
            }
        }

        ctx.attrSet("output", text);

        doWrite(ctx, data, text);
    }

    /**
     * 输出
     *
     * @param ctx  上下文
     * @param data 数据（原）
     * @param text 文源
     */
    protected void doWrite(Context ctx, Object data, String text) {
        if (data instanceof String && isWriteType() == false) {
            ctx.output(text);
        } else {
            //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
            if (ctx.contentTypeNew() == null) {
                ctx.contentType(serializer.mimeType());
            }

            ctx.output(text);
        }
    }

    @Override
    public boolean canRead(Context ctx, String mime) {
        return serializer.matched(ctx, mime);
    }
}