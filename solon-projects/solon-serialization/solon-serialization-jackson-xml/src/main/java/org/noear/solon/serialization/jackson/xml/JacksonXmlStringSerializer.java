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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import org.noear.solon.Utils;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.JsonContextSerializer;
import org.noear.solon.serialization.jackson.xml.impl.TypeReferenceImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Jackson xml 序列化
 *
 * @author painter
 * @since 2.8
 */
public class JacksonXmlStringSerializer implements JsonContextSerializer {
    public static final String label = "/xml";
    private JacksonXmlDecl<SerializationFeature> serializeConfig;
    private JacksonXmlDecl<DeserializationFeature> deserializeConfig;

    private AtomicBoolean initStatus = new AtomicBoolean(false);


    /**
     * 获取序列化配置
     */
    public JacksonXmlDecl<SerializationFeature> getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new JacksonXmlDecl<>();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public JacksonXmlDecl<DeserializationFeature> getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new JacksonXmlDecl<>();
        }

        return deserializeConfig;
    }


    /**
     * 初始化
     */
    protected void init() {
        if (initStatus.compareAndSet(false, true)) {
            getSerializeConfig().refresh();
            getDeserializeConfig().refresh();
        }
    }

    /**
     * 获取内容类型
     */
    @Override
    public String mimeType() {
        return "text/xml";
    }

    /**
     * 数据类型
     *
     */
    @Override
    public Class<String> dataType() {
        return String.class;
    }

    /**
     * 是否匹配
     *
     * @param ctx  请求上下文
     * @param mime 内容类型
     */
    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "jackson-xml";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        init();

        return getSerializeConfig().getMapper().writeValueAsString(obj);
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        init();

        if (toType == null) {
            return getDeserializeConfig().getMapper().readTree(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            TypeReferenceImpl typeRef = new TypeReferenceImpl(toType);
            return getDeserializeConfig().getMapper().readValue(data, typeRef);
        }
    }

    /**
     * 序列化主体
     *
     * @param ctx  请求上下文
     * @param data 数据
     */
    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        init();

        //如果没有设置过，用默认的 //如 ndjson,sse 或故意改变 mime（可由外部控制）
        if (ctx.contentTypeNew() == null) {
            ctx.contentType(this.mimeType());
        }

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    /**
     * 反序列化主体
     *
     * @param ctx 请求上下文
     */
    @Override
    public Object deserializeFromBody(Context ctx, @Nullable Type bodyType) throws IOException {
        init();

        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getDeserializeConfig().getMapper().readTree(data);
        } else {
            return null;
        }
    }

    /**
     * 添加编码器
     *
     * @param clz     类型
     * @param encoder 编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        getSerializeConfig().getCustomModule().addSerializer(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addEncoder(Class<T> clz, Converter<T, Object> converter) {
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