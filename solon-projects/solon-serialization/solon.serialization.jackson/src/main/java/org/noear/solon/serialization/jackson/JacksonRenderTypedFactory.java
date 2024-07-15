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
package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.StringSerializerRender;

/**
 * Json 类型化渲染器工厂
 *
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class JacksonRenderTypedFactory extends JacksonRenderFactoryBase {
    private ObjectMapper config = new ObjectMapper();

    public JacksonRenderTypedFactory(){
        config.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        config.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        config.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        config.activateDefaultTypingAsProperty(
                config.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        config.registerModule(new JavaTimeModule());
    }

    @Override
    public Render create() {
        registerModule();

        JacksonStringSerializer serializer = new JacksonStringSerializer();
        serializer.setConfig(config);

        return new StringSerializerRender(true, serializer);
    }

    @Override
    public ObjectMapper config() {
        return config;
    }
}
