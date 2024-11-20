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
package org.noear.solon.serialization.sbe;

import org.noear.solon.serialization.BytesSerializerRender;
import org.noear.solon.serialization.ContextSerializer;

/**
 * @author noear
 * @since 3.0
 * */
public class SbeRender extends BytesSerializerRender {
    private final SbeBytesSerializer serializer = new SbeBytesSerializer();

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
