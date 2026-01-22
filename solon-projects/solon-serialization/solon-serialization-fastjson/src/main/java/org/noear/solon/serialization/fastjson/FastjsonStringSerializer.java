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
package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.*;
import org.noear.solon.Utils;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntityStringSerializer;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil2;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Fastjson 字符串序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class FastjsonStringSerializer implements EntityStringSerializer {
    private static final String label = "/json";
    private static final FastjsonStringSerializer _default = new FastjsonStringSerializer();

    /**
     * 默认实例
     */
    public static FastjsonStringSerializer getDefault() {
        return _default;
    }

    private FastjsonDecl<SerializeConfig, SerializerFeature> serializeConfig;
    private FastjsonDecl<ParserConfig, Feature> deserializeConfig;

    public FastjsonStringSerializer(JsonProps jsonProps) {
        loadJsonProps(jsonProps);
    }

    public FastjsonStringSerializer() {

    }

    /**
     * 获取序列化配置
     */
    public FastjsonDecl<SerializeConfig, SerializerFeature> getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new FastjsonDecl<>(new SerializeConfig());
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public FastjsonDecl<ParserConfig, Feature> getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new FastjsonDecl<>(new ParserConfig());
        }

        return deserializeConfig;
    }

    /**
     * 内容类型
     */
    @Override
    public String mimeType() {
        return "application/json";
    }

    /**
     * 数据类型
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
            return mime.contains(label) || mime.startsWith(MimeType.APPLICATION_X_NDJSON_VALUE);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "fastjson-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        return JSON.toJSONString(obj,
                getSerializeConfig().getConfig(),
                new SerializeFilter[0], null,
                getSerializeConfig().getFeatures());

//        if (serializeConfig == null) {
//            return JSON.toJSONString(obj, serializerFeatures);
//        } else {
//            return JSON.toJSONString(obj, serializeConfig, new SerializeFilter[0], null, serializerFeatures);
//        }
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        if (toType == null) {
            return JSON.parse(data,
                    getDeserializeConfig().getConfig(),
                    getDeserializeConfig().getFeatures());

//            if (deserializeConfig == null) {
//                return JSON.parse(data, deserializeFeatures);
//            } else {
//                return JSON.parse(data, deserializeConfig, deserializeFeatures);
//            }

        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            return JSON.parseObject(data, toType,
                    getDeserializeConfig().getConfig(),
                    getDeserializeConfig().getFeatures());

//            if (deserializeConfig == null) {
//                return JSON.parseObject(data, toType, deserializeFeatures);
//            } else {
//                return JSON.parseObject(data, toType, deserializeConfig, deserializeFeatures);
//            }
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
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return JSON.parse(data,
                    getDeserializeConfig().getConfig(),
                    getDeserializeConfig().getFeatures());
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
    public void addEncoder(Type clz, ObjectSerializer encoder) {
        getSerializeConfig().getConfig().put(clz, encoder);

        if (clz == Long.class) {
            getSerializeConfig().getConfig().put(Long.TYPE, encoder);
        } else if (clz == Integer.class) {
            getSerializeConfig().getConfig().put(Integer.TYPE, encoder);
        }
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addEncoder(Class<T> clz, Converter<T, Object> converter) {
        addEncoder(clz, (ser, obj, fieldName, fieldType, features) -> {
            Object val = converter.convert((T) obj);

            SerializeWriter out = ser.getWriter();

            if (val == null) {
                out.writeNull();
            } else if (val instanceof String) {
                out.writeString((String) val);
            } else if (val instanceof Number) {
                if (val instanceof Integer || val instanceof Long) {
                    out.writeLong(((Number) val).longValue());
                } else {
                    out.writeDouble(((Number) val).doubleValue(), false);
                }
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + val.getClass().getName());
            }
        });
    }

    protected void loadJsonProps(JsonProps jsonProps) {
        if (jsonProps != null) {
            JsonPropsUtil2.dateAsFormat(this, jsonProps);
            JsonPropsUtil2.dateAsTicks(this, jsonProps);
            JsonPropsUtil2.boolAsInt(this, jsonProps);
            JsonPropsUtil2.longAsString(this, jsonProps);

            if (jsonProps.nullStringAsEmpty) {
                getSerializeConfig().addFeatures(SerializerFeature.WriteNullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                getSerializeConfig().addFeatures(SerializerFeature.WriteNullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                getSerializeConfig().addFeatures(SerializerFeature.WriteNullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                getSerializeConfig().addFeatures(SerializerFeature.WriteNullListAsEmpty);
            }

            if (jsonProps.nullAsWriteable) {
                getSerializeConfig().addFeatures(SerializerFeature.WriteMapNullValue);
            }

            if (!jsonProps.enumAsName) {
                getSerializeConfig().removeFeatures(SerializerFeature.WriteEnumUsingName);
            }
        }
    }
}