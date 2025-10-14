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
package org.noear.solon.serialization.snack4;

import org.noear.snack4.ONode;
import org.noear.snack4.Feature;
import org.noear.snack4.codec.ObjectEncoder;
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
 * Json 序列化器
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class Snack4StringSerializer implements EntityStringSerializer {
    private static final String label = "/json";
    private static final Snack4StringSerializer _default = new Snack4StringSerializer();

    /**
     * 默认实例
     */
    public static Snack4StringSerializer getDefault() {
        return _default;
    }

    private Snack4Decl serializeConfig;
    private Snack4Decl deserializeConfig;

    public Snack4StringSerializer(JsonProps jsonProps) {
        loadJsonProps(jsonProps);
    }

    public Snack4StringSerializer() {

    }

    /**
     * 获取序列化配置
     */
    public Snack4Decl getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new Snack4Decl();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public Snack4Decl getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new Snack4Decl();
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
        return "snack3-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        return ONode.serialize(obj, getSerializeConfig().getOptions());
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
            return ONode.deserialize(data, getDeserializeConfig().getOptions());
        } else {
            return ONode.deserialize(data, toType, getDeserializeConfig().getOptions());
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
            return ONode.ofJson(data, getDeserializeConfig().getOptions());
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
    public <T> void addEncoder(Class<T> clz, ObjectEncoder<T> encoder) {
        getSerializeConfig().getOptions().addEncoder(clz, encoder);
    }

    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addEncoder(Class<T> clz, Converter<T, Object> converter) {
        addEncoder(clz, (ctx, value, target) -> {
            Object val = converter.convert(value);
            if (val == null) {
                return target.setValue(null);
            } else if (val instanceof String) {
                return target.setValue(val);
            } else if (val instanceof Number) {
                return target.setValue(val);
            } else {
                throw new IllegalArgumentException("The result type of the converter is not supported: " + clz.getName());
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
                getSerializeConfig().addFeatures(Feature.Write_NullStringAsEmpty);
            }

            if (jsonProps.nullBoolAsFalse) {
                getSerializeConfig().addFeatures(Feature.Write_NullBooleanAsFalse);
            }

            if (jsonProps.nullNumberAsZero) {
                getSerializeConfig().addFeatures(Feature.Write_NullNumberAsZero);
            }

            if (jsonProps.nullArrayAsEmpty) {
                getSerializeConfig().addFeatures(Feature.Write_NullListAsEmpty);
            }

            if (jsonProps.nullAsWriteable) {
                getSerializeConfig().addFeatures(Feature.Write_Nulls);
            }

            if (jsonProps.enumAsName) {
                getSerializeConfig().addFeatures(Feature.Write_EnumUsingName);
            }
        }
    }
}