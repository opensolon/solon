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

import java.util.Base64;

/**
 * 字节组序列化渲染器
 *
 * @author noear
 * @since 2.8
 * @deprecated 3.6
 */
@Deprecated
public abstract class BytesSerializerRender implements Render {
    /**
     * 获取序列化器
     */
    public abstract EntitySerializer<byte[]> getSerializer();

    /**
     * 获取渲染器名字
     */
    @Override
    public String name() {
        return this.getClass().getSimpleName() + "#" + getSerializer().name();
    }

    /**
     * 匹配检测
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        return getSerializer().matched(ctx, mime);
    }

    /**
     * 渲染并返回
     */
    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] tmp = getSerializer().serialize(data);
        return Base64.getEncoder().encodeToString(tmp);
    }

    /**
     * 渲染并输出
     */
    @Override
    public void render(Object data, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", name());
        }

        getSerializer().serializeToBody(ctx, data);
    }
}
