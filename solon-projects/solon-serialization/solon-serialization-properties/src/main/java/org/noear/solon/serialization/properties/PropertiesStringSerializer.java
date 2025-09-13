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
package org.noear.solon.serialization.properties;

import org.noear.snack.ONode;
import org.noear.snack.core.NameValues;
import org.noear.snack.core.Options;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.lang.Nullable;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author noear
 * @since 2.7
 * @since 2.8
 */
public class PropertiesStringSerializer implements ContextSerializer<String> {
    private static final PropertiesStringSerializer _default = new PropertiesStringSerializer();

    public static PropertiesStringSerializer getDefault() {
        return _default;
    }


    private Options config;

    /**
     * 获取配置
     */
    public Options getConfig() {
        if (config == null) {
            config = Options.def();
        }

        return config;
    }

    /**
     * 设置配置
     */
    public void setConfig(Options config) {
        if (config != null) {
            this.config = config;
        }
    }

    /**
     * 内容类型
     */
    @Override
    public String mimeType() {
        return "application/properties";
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
        return false;
    }

    /**
     * 序列化器名字
     */
    @Override
    public String name() {
        return "properties-string";
    }

    /**
     * 序列化
     *
     * @param obj 对象
     */
    @Override
    public String serialize(Object obj) throws IOException {
        ONode oNode = ONode.loadObj(obj, getConfig());
        Properties oProperties = oNode.toObject(Properties.class);

        StringBuilder buf = new StringBuilder();
        List<String> bufKeys = new ArrayList<>(oProperties.stringPropertyNames());
        bufKeys.sort(String::compareTo);

        for (String key : bufKeys) {
            buf.append(key).append("=").append(oProperties.getProperty(key)).append("\n");
        }

        return buf.toString();
    }

    /**
     * 反序列化
     *
     * @param data   数据
     * @param toType 目标类型
     */
    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        Properties prop = Utils.buildProperties(data);

        if (toType == null) {
            return prop;
        } else {
            ONode oNode = ONode.loadObj(prop, getConfig());
            return oNode.toObject(toType);
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
        NameValues nameValues = new NameValues();
        for (KeyValues<String> kv : ctx.paramMap()) {
            for (String val : kv.getValues()) {
                nameValues.add(kv.getKey(), val);
            }
        }

        return ONode.loadObj(nameValues, getConfig());
    }
}
