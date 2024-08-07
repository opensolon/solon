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
package org.noear.solon.serialization.gson;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 渲染器工厂
 *
 * @author noear
 * @since 1.5
 */
public class GsonRenderFactory extends GsonRenderFactoryBase {
    private final GsonStringSerializer serializer = new GsonStringSerializer();

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        serializer.getConfig().registerTypeAdapter(clz, encoder);
    }

    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer);
    }

    @Override
    public GsonBuilder config() {
        return serializer.getConfig();
    }
}
