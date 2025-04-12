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

import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.core.serialize.Serializer;

/**
 * 字符串序列化渲染器
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class StringSerializerRender implements Render {
    private transient boolean typed;
    private transient ContextSerializer<String> serializer;


    public StringSerializerRender(boolean typed, ContextSerializer<String> serializer) {
        this.typed = typed;
        this.serializer = serializer;
    }

    /**
     * 是否类型化的
     */
    public boolean isTyped() {
        return typed;
    }

    /**
     * 获取序列化器
     */
    public Serializer<String> getSerializer() {
        return serializer;
    }

    /**
     * 获取渲染器名字
     */
    @Override
    public String name() {
        return this.getClass().getSimpleName() + "#" + serializer.name();
    }

    /**
     * 匹配检测
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (typed) {
            return false;
        } else {
            return serializer.matched(ctx, mime);
        }
    }

    /**
     * 渲染并返回
     *
     * @param data 数据
     * @param ctx  上下文
     */
    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        return serializer.serialize(data);
    }

    /**
     * 渲染
     *
     * @param data 数据
     * @param ctx  上下文
     */
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", name());
        }

        String text = null;

        if (typed) {
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

        output(ctx, data, text);
    }

    /**
     * 输出
     *
     * @param ctx  上下文
     * @param data 数据（原）
     * @param text 文源
     */
    protected void output(Context ctx, Object data, String text) {
        if (data instanceof String && isTyped() == false) {
            ctx.output(text);
        } else {
            //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
            if (ctx.contentTypeNew() == null) {
                ctx.contentType(serializer.mimeType());
            }

            ctx.output(text);
        }
    }
}