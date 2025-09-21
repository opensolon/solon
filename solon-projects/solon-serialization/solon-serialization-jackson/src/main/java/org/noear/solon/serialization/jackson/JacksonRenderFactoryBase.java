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
package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * @author noear
 * @since 1.5
 * @deprecated 3.6
 * */
@Deprecated
public abstract class JacksonRenderFactoryBase implements JsonRenderFactory {
    protected final JacksonStringSerializer serializer;

    public JacksonRenderFactoryBase(JacksonStringSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 获取序列化器
     */
    public JacksonStringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public ObjectMapper config() {
        return getSerializer().getSerializeConfig().getMapper();
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        serializer.addEncoder(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T, Object> converter) {
        serializer.addEncoder(clz, converter);
    }
}