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
package org.noear.solon.serialization.gson;

import com.google.gson.*;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * @author noear
 * @since 1.5
 */
public abstract class GsonRenderFactoryBase implements JsonRenderFactory {
    protected final GsonStringSerializer serializer = new GsonStringSerializer();

    /**
     * 获取序列化器
     */
    public GsonStringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public GsonBuilder config() {
        return serializer.getConfig();
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        config().registerTypeAdapter(clz, encoder);
    }


    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T,Object> converter) {
        addEncoder(clz, (source, type, jsc) -> {
            Object val = converter.convert((T) source);

            if (val == null) {
                return JsonNull.INSTANCE;
            } else if (val instanceof String) {
                return new JsonPrimitive((String) val);
            } else if (val instanceof Number) {
                return new JsonPrimitive((Number) val);
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}
