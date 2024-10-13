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
package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.util.ClassUtil;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Jackson xml 序列化
 *
 * @author painter
 * @since 2.8
 */
public class JacksonXmlStringSerializer implements ContextSerializer<String> {
    public static final String label = "/xml";
    private XmlMapper config;
    private Set<SerializationFeature> configFeatures;
    private SimpleModule configModule;

    private AtomicBoolean initStatus = new AtomicBoolean(false);

    /**
     * 获取定制特性
     */
    public Set<SerializationFeature> getCustomFeatures() {
        if (configFeatures == null) {
            configFeatures = new HashSet<>();
        }

        return configFeatures;
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
     * 获取配置
     */
    public XmlMapper getConfig() {
        if (config == null) {
            config = new XmlMapper();
        }

        return config;
    }

    /**
     * 设置配置
     */
    public void setConfig(XmlMapper config) {
        if (config != null) {
            this.config = config;
        }
    }

    /**
     * 初始化
     */
    protected void init() {
        if (initStatus.compareAndSet(false, true)) {
            if (configFeatures != null) {
                for (SerializationFeature f1 : configFeatures) {
                    getConfig().enable(f1);
                }
            }

            if (configModule != null) {
                getConfig().registerModule(configModule);
            }
        }
    }

    /**
     * 获取内容类型
     */
    @Override
    public String contentType() {
        return "text/xml";
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

        return getConfig().writeValueAsString(obj);
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
            return getConfig().readTree(data);
        } else {
            Class<?> clz = ClassUtil.getTypeClass(toType);
            return getConfig().readValue(data, clz);
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

        ctx.contentType(contentType());

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
    public Object deserializeFromBody(Context ctx) throws IOException {
        init();

        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getConfig().readTree(data);
        } else {
            return null;
        }
    }
}