/*
 * Copyright 2017-2024 noear.org and authors
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
import org.noear.solon.core.util.ClassUtil;
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

    public JSONWriter.Context getSerializeConfig() {
        if (serializeConfig == null) {
            serializeConfig = new JSONWriter.Context(new ObjectWriterProvider());
        }

        return serializeConfig;
    }

    /**
     * 配置序列化特性
     * */
    public void cfgSerializeFeatures(boolean isReset, boolean isAdd, JSONWriter.Feature... features) {
        if (isReset) {
            getSerializeConfig().setFeatures(JSONFactory.getDefaultWriterFeatures());
        }

        for (JSONWriter.Feature feature : features) {
            getSerializeConfig().config(feature, isAdd);
        }
    }

    public JSONReader.Context getDeserializeConfig() {
        if (deserializeConfig == null) {
            deserializeConfig = new JSONReader.Context(new ObjectReaderProvider());
        }
        return deserializeConfig;
    }

    public void cfgDeserializeFeatures(boolean isReset, boolean isAdd, JSONReader.Feature... features) {
        if (isReset) {
            getDeserializeConfig().setFeatures(JSONFactory.getDefaultReaderFeatures());
        }

        for (JSONReader.Feature feature : features) {
            getDeserializeConfig().config(feature, isAdd);
        }
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label);
        }
    }

    @Override
    public String name() {
        return "fastjson2-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return JSON.toJSONString(obj, getSerializeConfig());
    }

    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        if (toType == null) {
            return JSON.parse(data, getDeserializeConfig());
        } else {
            return JSON.parseObject(data, toType, getDeserializeConfig());
        }
    }

    @Override
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(getContentType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return JSON.parse(data, getDeserializeConfig());
        } else {
            return null;
        }
    }
}