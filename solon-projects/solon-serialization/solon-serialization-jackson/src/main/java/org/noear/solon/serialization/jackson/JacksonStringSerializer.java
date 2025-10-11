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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;
import org.noear.solon.serialization.jackson.impl.TypeReferenceImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Jackson json 序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class JacksonStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";
    private ObjectMapper serializeConfig;
    private ObjectMapper deserializeConfig;
    private Set<SerializationFeature> serializeFeatures;
    private Set<DeserializationFeature> deserializeFeatures;

    private SimpleModule configModule;

    private AtomicBoolean initStatus = new AtomicBoolean(false);

    /**
     * 获取序列化定制特性
     */
    public Set<SerializationFeature> getSerializeFeatures() {
        if (serializeFeatures == null) {
            serializeFeatures = new HashSet<>();
        }

        return serializeFeatures;
    }

    /**
     * 获取反序列化定制特性
     */
    public Set<DeserializationFeature> getDeserializeFeatures() {
        if (deserializeFeatures == null) {
            deserializeFeatures = new HashSet<>();
        }

        return deserializeFeatures;
    }

    /**
     * 获取序列化配置
     */
    public ObjectMapper getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new ObjectMapper();
        }

        return serializeConfig;
    }

    /**
     * 获取反序列化配置
     */
    public ObjectMapper getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new ObjectMapper();
        }

        return deserializeConfig;
    }

    /**
     * 设置序列化配置
     */
    public void setSerializeConfig(ObjectMapper config) {
        if (config != null) {
            this.serializeConfig = config;
        }
    }

    /**
     * 设置反序列化配置
     */
    public void setDeserializeConfig(ObjectMapper config) {
        if (config != null) {
            this.deserializeConfig = config;
        }
    }

    /**
     * 获取模块特性
     */
    public SimpleModule getCustomModule() {
        if (configModule == null) {
            configModule = new SimpleModule();
        }

        return configModule;
    }

    /**
     * 初始化
     */
    protected void init() {
        if (initStatus.compareAndSet(false, true)) {
            if (serializeFeatures != null) {
                for (SerializationFeature f1 : serializeFeatures) {
                    getSerializeConfig().enable(f1);
                }
            }

            if (deserializeFeatures != null) {
                for (DeserializationFeature f1 : deserializeFeatures) {
                    getDeserializeConfig().enable(f1);
                }
            }

            if (configModule != null) {
                getSerializeConfig().registerModule(configModule);
            }
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
     * */
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
        return "jackson-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        init();
        return getSerializeConfig().writeValueAsString(obj);
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
            return getDeserializeConfig().readTree(data);
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            TypeReferenceImpl typeRef = new TypeReferenceImpl(toType);
            return getDeserializeConfig().readValue(data, typeRef);
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
            return getDeserializeConfig().readTree(data);
        } else {
            return null;
        }
    }
}