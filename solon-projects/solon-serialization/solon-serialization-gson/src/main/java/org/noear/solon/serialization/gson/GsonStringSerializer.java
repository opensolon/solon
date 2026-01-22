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
import org.noear.solon.Utils;
import org.noear.solon.core.convert.Converter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.EntityStringSerializer;
import org.noear.solon.serialization.gson.impl.*;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil2;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

/**
 * Gson 字符串序列化
 *
 * @author noear
 * @since 2.8
 */
public class GsonStringSerializer implements EntityStringSerializer {
    private static final String label = "/json";
    private static final GsonStringSerializer _default = new GsonStringSerializer();

    /**
     * 默认实例
     */
    public static GsonStringSerializer getDefault() {
        return _default;
    }


    private GsonDecl serializeConfig;
    private GsonDecl deserializeConfig;

    public GsonStringSerializer(JsonProps jsonProps){
        loadJsonProps(jsonProps);
    }

    public GsonStringSerializer(){

    }

    /**
     * 获取序列化配置
     */
    public GsonDecl getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new GsonDecl();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public GsonDecl getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new GsonDecl();
        }

        return deserializeConfig;
    }

    /**
     * 刷新
     */
    public void refresh() {
        if (serializeConfig != null) {
            serializeConfig.refresh(true);
        }

        if (deserializeConfig != null) {
            deserializeConfig.refresh(true);
        }
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
            return mime.contains(label) || mime.startsWith(MimeType.APPLICATION_X_NDJSON_VALUE);
        }
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "gson-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        return getSerializeConfig().getGson().toJson(obj);
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
            return JsonParser.parseString(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            JsonElement jsonElement = JsonParser.parseString(data);
            return getDeserializeConfig().getGson().fromJson(jsonElement, toType);
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
            return JsonParser.parseString(data);
        } else {
            return null;
        }
    }


    /**
     * 反序列化
     */
    public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonSyntaxException {
        return getDeserializeConfig().getGson().fromJson(json, typeOfT);
    }

    /**
     * 添加编码器
     */
    public <T> void addEncoder(Class<T> clz, JsonSerializer<T> encoder) {
        getSerializeConfig().getBuilder().registerTypeAdapter(clz, encoder);
    }


    /**
     * 添加转换器（编码器的简化版）
     *
     * @param clz       类型
     * @param converter 转换器
     */
    @Override
    public <T> void addEncoder(Class<T> clz, Converter<T, Object> converter) {
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

    protected void loadJsonProps(JsonProps jsonProps) {
        boolean writeNulls = false;

        if (jsonProps != null) {
            JsonPropsUtil2.dateAsFormat(this, jsonProps);
            JsonPropsUtil2.dateAsTicks(this, jsonProps);
            JsonPropsUtil2.boolAsInt(this, jsonProps);
            JsonPropsUtil2.longAsString(this, jsonProps);

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;


            if (writeNulls) {
                this.getSerializeConfig().getBuilder().serializeNulls();
            }

            if (jsonProps.nullNumberAsZero) {
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Short.class, new NullNumberWriteAdapter<Short>());
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Integer.class, new NullNumberWriteAdapter<Integer>());

                this.getSerializeConfig().getBuilder().registerTypeAdapter(Long.class, new NullLongWriteAdapter(jsonProps));

                this.getSerializeConfig().getBuilder().registerTypeAdapter(Float.class, new NullNumberWriteAdapter<Float>());
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Double.class, new NullNumberWriteAdapter<Double>());
            }

            if (jsonProps.nullArrayAsEmpty) {
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Collection.class, new NullCollectionSerialize());
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Arrays.class, new NullArraySerialize());
            }

            if (jsonProps.nullBoolAsFalse) {
                this.getSerializeConfig().getBuilder().registerTypeAdapter(Boolean.class, new NullBooleanWriteAdapter(jsonProps));
            }

            if (jsonProps.nullStringAsEmpty) {
                this.getSerializeConfig().getBuilder().registerTypeAdapter(String.class, new NullStringWriteAdapter());
            }

            if (!jsonProps.enumAsName) {
                this.getSerializeConfig().getBuilder().registerTypeHierarchyAdapter(Enum.class, new EnumWriteAdapter(false));
            }

        } else {
            //默认为时间截
            this.getSerializeConfig().getBuilder().registerTypeAdapter(Date.class, new DateSerialize());
        }
    }
}