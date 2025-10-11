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
package org.noear.solon.serialization.jackson.xml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;
import org.noear.solon.serialization.jackson.xml.impl.NullBeanSerializerModifierImpl;
import org.noear.solon.serialization.prop.JsonProps;
import org.noear.solon.serialization.prop.JsonPropsUtil;

import java.util.Arrays;

import static com.fasterxml.jackson.databind.MapperFeature.PROPAGATE_TRANSIENT_MARKER;
import static com.fasterxml.jackson.databind.MapperFeature.SORT_PROPERTIES_ALPHABETICALLY;

/**
 * Xml 渲染器工厂
 *
 * @author painter
 * @since 2.8
 */
public class JacksonXmlRenderFactory extends JacksonXmlRenderFactoryBase {

    public JacksonXmlRenderFactory(JacksonXmlStringSerializer serializer, JsonProps jsonProps) {
        super(serializer);
        serializer.getSerializeFeatures().add(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        serializer.getSerializeConfig().registerModule(new JavaTimeModule());
        applyProps(jsonProps);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_XML};
    }

    /**
     * 创建
     */
    @Override
    public Render create() {
        return new StringSerializerRender(false, serializer);
    }


    /**
     * 重新设置特性
     */
    public void setFeatures(SerializationFeature... features) {
        serializer.getSerializeFeatures().clear();
        serializer.getSerializeFeatures().addAll(Arrays.asList(features));
    }

    /**
     * 添加特性
     */
    public void addFeatures(SerializationFeature... features) {
        serializer.getSerializeFeatures().addAll(Arrays.asList(features));
    }

    /**
     * 移除特性
     */
    public void removeFeatures(SerializationFeature... features) {
        serializer.getSerializeFeatures().removeAll(Arrays.asList(features));
    }

    protected void applyProps(JsonProps jsonProps) {
        boolean writeNulls = false;

        if (jsonProps != null) {
            JsonPropsUtil.dateAsFormat(this, jsonProps);
            JsonPropsUtil.dateAsTicks(this, jsonProps);
            JsonPropsUtil.boolAsInt(this, jsonProps);
            JsonPropsUtil.longAsString(this, jsonProps);

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
