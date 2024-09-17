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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.jackson.xml.impl.NullBeanSerializerModifierImpl;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

/**
 * Xml 渲染器工厂
 *
 * @author painter
 * @since 2.8
 */
public class JacksonXmlRenderFactory extends JacksonXmlRenderFactoryBase {
    private XmlMapper config = new XmlMapper();
    private Set<SerializationFeature> features;

    public JacksonXmlRenderFactory(JsonProps jsonProps) {
        features = new HashSet<>();
        features.add(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.registerModule(new JavaTimeModule());
        applyProps(jsonProps);
    }

    @Override
    public String[] mappings() {
        return new String[]{"@xml"};
    }

    @Override
    public Render create() {
        registerModule();

        for (SerializationFeature f1 : features) {
            config.enable(f1);
        }

        JacksonXmlStringSerializer serializer = new JacksonXmlStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(false, serializer);
    }

    @Override
    public XmlMapper config() {
        return config;
    }


    /**
     * 重新设置特性
     */
    public void setFeatures(SerializationFeature... features) {
        this.features.clear();
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     */
    public void addFeatures(SerializationFeature... features) {
        this.features.addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     */
    public void removeFeatures(SerializationFeature... features) {
        this.features.removeAll(Arrays.asList(features));
    }

    protected void applyProps(JsonProps jsonProps) {
        boolean writeNulls = false;

        if (JsonPropsUtil.apply(this, jsonProps)) {

            if (jsonProps.longAsString) {
                this.addConvertor(Long.class, String::valueOf);
                this.addConvertor(long.class, String::valueOf);
            }

            writeNulls = jsonProps.nullAsWriteable ||
                    jsonProps.nullNumberAsZero ||
                    jsonProps.nullArrayAsEmpty ||
                    jsonProps.nullBoolAsFalse ||
                    jsonProps.nullStringAsEmpty;

            if (writeNulls) {
                // Xml配置处理,基本都是各类型空值写入
                SerializerFactory serializerFactory = BeanSerializerFactory.instance.withSerializerModifier(new NullBeanSerializerModifierImpl(jsonProps));
                this.config().setSerializerFactory(serializerFactory);
            }

            if(jsonProps.enumAsName){
                this.config().configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING,true);
            }
        }

        if (writeNulls == false) {
            this.config().setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }

        //启用 transient 关键字
        this.config().configure(PROPAGATE_TRANSIENT_MARKER,true);
        //启用排序（即使用 LinkedHashMap）
        this.config().configure(SORT_PROPERTIES_ALPHABETICALLY, true);
        //是否识别不带引号的key
        this.config().configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        //是否识别单引号的key
        this.config().configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //浮点数默认类型（dubbod 转 BigDecimal）
        this.config().configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);


        //反序列化时候遇到不匹配的属性并不抛出异常
        this.config().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时候遇到空对象不抛出异常
        this.config().configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        //反序列化的时候如果是无效子类型,不抛出异常
        this.config().configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
    }
}
