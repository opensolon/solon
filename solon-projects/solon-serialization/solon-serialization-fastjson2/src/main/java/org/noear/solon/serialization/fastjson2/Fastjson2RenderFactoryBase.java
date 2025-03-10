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
package org.noear.solon.serialization.fastjson2;


import com.alibaba.fastjson2.writer.ObjectWriter;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

/**
 * Json 渲染器工厂基类
 *
 * @author noear
 * @author 暮城留风
 * @since 1.10
 */
public abstract class Fastjson2RenderFactoryBase implements JsonRenderFactory {
    protected Fastjson2StringSerializer serializer = new Fastjson2StringSerializer();

    public Fastjson2RenderFactoryBase(){
        //默认时间处理为时间戳
        serializer.getSerializeConfig().setDateFormat("millis");
    }

    /**
     * 获取序列化器
     */
    public Fastjson2StringSerializer getSerializer() {
        return serializer;
    }

    /**
     * 序列化配置
     */
    public ObjectWriterProvider config(){
        return serializer.getSerializeConfig().getProvider();
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, ObjectWriter encoder) {
        config().register(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T, Object> converter) {
        addEncoder(clz, (out, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);
            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Long) {
                    out.writeInt64(((Number) val).longValue());
                } else if (val instanceof Integer) {
                    out.writeInt32(((Number) val).intValue());
                } else if (val instanceof Float) {
                    out.writeDouble(((Number) val).floatValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue());
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }
}