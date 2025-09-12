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
package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.serialization.JsonRenderFactory;

import java.io.IOException;
import java.util.Date;

/**
 * @author painter
 * @since 2.8
 */
public abstract class JacksonXmlRenderFactoryBase implements JsonRenderFactory {
    protected final JacksonXmlStringSerializer serializer;
    public JacksonXmlRenderFactoryBase(JacksonXmlStringSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 获取序列化器
     */
    public JacksonXmlStringSerializer getSerializer(){
        return serializer;
    }

    /**
     * 序列化配置
     */
    public XmlMapper config() {
        return getSerializer().getConfig();
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        serializer.getCustomModule().addSerializer(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addConvertor(Class<T> clz, Converter<T,Object> converter) {
        if (clz == Date.class) {
            addEncoder(Date.class, new DateSerializer() {
                public void serialize(Date date, JsonGenerator out, SerializerProvider sp) throws IOException {
                    if (this._customFormat == null) {
                        writeDefaultValue(converter, clz.cast(date), out);
                    } else {
                        super.serialize(date, out, sp);
                    }

                }
            });
        } else {
            addEncoder(clz, new JsonSerializer<T>() {
                @Override
                public void serialize(T source, JsonGenerator out, SerializerProvider sp) throws IOException {
                    writeDefaultValue(converter, source, out);
                }
            });
        }
    }

    private static <T> void writeDefaultValue(Converter<T, Object> converter, T source, JsonGenerator out) throws IOException {
        Object val = converter.convert(source);

        if (val == null) {
            out.writeNull();
        } else if (val instanceof String) {
            out.writeString((String) val);
        } else if (val instanceof Number) {
            if (val instanceof Integer || val instanceof Long) {
                out.writeNumber(((Number) val).longValue());
            } else {
                out.writeNumber(((Number) val).doubleValue());
            }
        } else {
            throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
        }
    }
}
