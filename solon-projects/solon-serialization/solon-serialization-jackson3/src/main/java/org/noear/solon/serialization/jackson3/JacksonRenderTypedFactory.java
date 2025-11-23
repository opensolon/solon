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
package org.noear.solon.serialization.jackson3;

import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializerNames;
import org.noear.solon.serialization.StringSerializerRender;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;

import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 * @deprecated 3.5
 */
@Deprecated
public class JacksonRenderTypedFactory extends Jackson3RenderFactoryBase {
    public JacksonRenderTypedFactory() {
        super(new Jackson3StringSerializer());
        ObjectMapper mapper = serializer.getSerializeConfig().getMapper();
        mapper.serializationConfig().with(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.deserializationConfig().without(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        serializer.getSerializeConfig().getMapper().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        serializer.getSerializeConfig().getMapper().activateDefaultTypingAsProperty(
                serializer.getSerializeConfig().getMapper().getPolymorphicTypeValidator(),
                DefaultTyping.JAVA_LANG_OBJECT, "@type");
        mapper.registeredModules().add(new JavaTimeModule());
        serializer.getSerializeConfig().setMapper(mapper);
    }

    /**
     * 后缀或名字映射
     */
    @Override
    public String[] mappings() {
        return new String[]{SerializerNames.AT_JSON_TYPED};
    }

    /**
     * 创建
     */
    @Override
    public Render create() {
        return new StringSerializerRender(true, serializer);
    }
}