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
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Fastjson 字符串序列化
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class FastjsonStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

    private SerializeConfig serializeConfig;
    private int serializerFeatures = JSON.DEFAULT_GENERATE_FEATURE;
    private ParserConfig deserializeConfig;
    private int deserializeFeatures = JSON.DEFAULT_PARSER_FEATURE;

    /**
     * 获取序列化配置
     */
    public SerializeConfig getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new SerializeConfig();
        }

        return serializeConfig;
    }

    /**
     * 配置序列化特性
     *
     * @param isReset  是否重置
     * @param isAdd    是否添加
     * @param features 特性
     */
    public void cfgSerializerFeatures(boolean isReset, boolean isAdd, SerializerFeature... features) {
        if (isReset) {
            serializerFeatures = JSON.DEFAULT_GENERATE_FEATURE;
        }

        for (SerializerFeature feature : features) {
            if (isAdd) {
                serializerFeatures |= feature.getMask();
            } else {
                serializerFeatures &= ~feature.getMask();
            }
        }
    }

    /**
     * 获取反序列化配置
     */
    public ParserConfig getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new ParserConfig();
        }

        return deserializeConfig;
    }

    /**
     * 配置反序列化特性
     *
     * @param isReset  是否重置
     * @param isAdd    是否添加
     * @param features 特性
     */
    public void cfgDeserializeFeatures(boolean isReset, boolean isAdd, Feature... features) {
        if (isReset) {
            deserializeFeatures = JSON.DEFAULT_GENERATE_FEATURE;
        }

        for (Feature feature : features) {
            if (isAdd) {
                deserializeFeatures |= feature.getMask();
            } else {
                deserializeFeatures &= ~feature.getMask();
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
        if (serializeConfig == null) {
            return JSON.toJSONString(obj, serializerFeatures);
        } else {
            return JSON.toJSONString(obj, serializeConfig, new SerializeFilter[0], null, serializerFeatures);
        }
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
            if (deserializeConfig == null) {
                return JSON.parse(data, deserializeFeatures);
            } else {
                return JSON.parse(data, deserializeConfig, deserializeFeatures);
            }
        } else {
            if (toType instanceof Class) {
                //处理匿名名类
                Class<?> toClz = (Class<?>) toType;
                if (toClz.isAnonymousClass()) {
                    toType = toClz.getGenericSuperclass();
                }
            }

            if (deserializeConfig == null) {
                return JSON.parseObject(data, toType, deserializeFeatures);
            } else {
                return JSON.parseObject(data, toType, deserializeConfig, deserializeFeatures);
            }
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
        ctx.contentType(this.mimeType());

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
            if (deserializeConfig == null) {
                return JSON.parse(data, deserializeFeatures);
            } else {
                return JSON.parse(data, deserializeConfig, deserializeFeatures);
            }
        } else {
            return null;
        }
    }
}