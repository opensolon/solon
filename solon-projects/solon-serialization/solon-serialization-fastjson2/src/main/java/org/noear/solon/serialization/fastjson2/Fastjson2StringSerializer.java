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

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.reader.ObjectReaderProvider;
import com.alibaba.fastjson2.writer.ObjectWriterProvider;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.MimeType;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Fastjson2 字符串序列化
 *
 * @author noear
 * @author 暮城留风
 * @since 1.10
 * @since 2.8
 */
public class Fastjson2StringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

    private JSONWriter.Context serializeConfig;
    private JSONReader.Context deserializeConfig;

    /**
     * 获取序列化配置
     */
    public JSONWriter.Context getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new JSONWriter.Context(new ObjectWriterProvider());
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
    public void cfgSerializeFeatures(boolean isReset, boolean isAdd, JSONWriter.Feature... features) {
        if (isReset) {
            getSerializeConfig().setFeatures(JSONFactory.getDefaultWriterFeatures());
        }

        for (JSONWriter.Feature feature : features) {
            getSerializeConfig().config(feature, isAdd);
        }
    }

    /**
     * 获取反序列化配置
     */
    public JSONReader.Context getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new JSONReader.Context(new ObjectReaderProvider());
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
    public void cfgDeserializeFeatures(boolean isReset, boolean isAdd, JSONReader.Feature... features) {
        if (isReset) {
            getDeserializeConfig().setFeatures(JSONFactory.getDefaultReaderFeatures());
        }

        for (JSONReader.Feature feature : features) {
            getDeserializeConfig().config(feature, isAdd);
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
        return "fastjson2-json";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        return JSON.toJSONString(obj, getSerializeConfig());
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
            return JSON.parse(data, getDeserializeConfig());
        } else {
            return JSON.parseObject(data, toType, getDeserializeConfig());
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
            return JSON.parse(data, getDeserializeConfig());
        } else {
            return null;
        }
    }
}