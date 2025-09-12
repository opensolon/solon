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

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

/**
 * Protostuff 渲染器
 *
 * @author noear
 * @since 1.2
 * @since 2.8
 */
public class ProtostuffRender extends BytesSerializerRender {
    private final ProtostuffBytesSerializer serializer;

    public ProtostuffRender(ProtostuffBytesSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 获取序列化器
     */
    @Override
    public ContextSerializer<byte[]> getSerializer() {
        return serializer;
    }

    /**
     * 获取渲染器名字
     */
    @Override
    public String name() {
        return this.getClass().getSimpleName();
    }
}