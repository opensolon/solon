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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Gson 字符串序列化
 *
 * @author noear
 * @since 2.8
 */
public class GsonStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";
    private GsonBuilder serializeConfig;
    private GsonBuilder deserializeConfig;
    private Gson serializeGson;
    private Gson deserializeGson;

    /**
     * 获取序列化配置
     */
    public GsonBuilder getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new GsonBuilder();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public GsonBuilder getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new GsonBuilder();
        }

        return deserializeConfig;
    }

    /**
     * 刷新
     */
    public void refresh() {
        serializeGson = getSerializeConfig().create();
        deserializeGson = getDeserializeConfig().create();
    }


    public Gson getSerializeGson() {
        if (serializeGson == null) {
            Utils.locker().lock();

            try {
                if (serializeGson == null) {
                    serializeGson = getSerializeConfig().create();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return serializeGson;
    }

    public Gson getDeserializeGson() {
        if (deserializeGson == null) {
            Utils.locker().lock();

            try {
                if (deserializeGson == null) {
                    deserializeGson = getDeserializeConfig().create();
                }
            } finally {
                Utils.locker().unlock();
            }
        }

        return deserializeGson;
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
        return getSerializeGson().toJson(obj);
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
            return getDeserializeGson().fromJson(jsonElement, toType);
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
}