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

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Xml 渲染器工厂
 *
 * @author painter
 * @since 2.8
 * @deprecated 3.6
 * */
@Deprecated
public class JacksonXmlRenderFactory extends JacksonXmlRenderFactoryBase {

    public JacksonXmlRenderFactory(JacksonXmlEntityConverter entityConverter) {
        super(entityConverter.getSerializer());
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
        serializer.getSerializeConfig().setFeatures(features);
    }

    /**
     * 添加特性
     */
    public void addFeatures(SerializationFeature... features) {
        serializer.getSerializeConfig().addFeatures(features);
    }

    /**
     * 移除特性
     */
    public void removeFeatures(SerializationFeature... features) {
        serializer.getSerializeConfig().removeFeatures(features);
    }
}