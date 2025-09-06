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
package org.noear.nami.coder.jackson;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.noear.nami.*;
import org.noear.nami.coder.jackson.impl.TimeDeserializer;
import org.noear.nami.common.ContentTypes;
import org.noear.nami.exception.NamiDecodeException;
import org.noear.solon.Utils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * @author noear
 * @since 1.2
 */
public class JacksonDecoder implements Decoder {
    public static final JacksonDecoder instance = new JacksonDecoder();

    private ObjectMapper mapper_type;

    public JacksonDecoder() {
        mapper_type = newMapper(new JavaTimeModule());

        addDeserializer(LocalDateTime.class, new TimeDeserializer<>(LocalDateTime.class));
        addDeserializer(LocalDate.class, new TimeDeserializer<>(LocalDate.class));
        addDeserializer(LocalTime.class, new TimeDeserializer<>(LocalTime.class));
        addDeserializer(Date.class, new TimeDeserializer<>(Date.class));

        mapper_type.registerModule(this.configModule);
    }

    public ObjectMapper newMapper(com.fasterxml.jackson.databind.Module... modules){
        ObjectMapper mapper = new ObjectMapper();

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.activateDefaultTypingAsProperty(
                mapper.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT, "@type");
        mapper.registerModules(modules);
        // 允许使用未带引号的字段名
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许使用单引号
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);

        return mapper;
    }


    private SimpleModule configModule = new SimpleModule();

    /**
     * 添加反序列化器
     * */
    public <T> void addDeserializer(Class<T> clz, JsonDeserializer<? extends T> deser) {
        configModule.addDeserializer(clz, deser);
    }

    @Override
    public String enctype() {
        return ContentTypes.JSON_VALUE;
    }

    @Override
    public <T> T decode(Result rst, Type type) throws Exception {
        if (rst.body().length == 0) {
            return null;
        }

        String str = rst.bodyAsString();
        if ("null".equals(str)) {
            return null;
        }

        try {
            if (str.contains("\"stackTrace\":[{")) {
                return (T) mapper_type.readValue(str, RuntimeException.class);
            } else {
                if (String.class == type && Utils.isNotEmpty(str)) {
                    if (str.charAt(0) != '\'' && str.charAt(0) != '"') {
                        return (T) str;
                    }
                }

                return (T) mapper_type.readValue(str, new TypeReferenceImp(type));
            }
        } catch (Throwable ex) {
            throw new NamiDecodeException("Decoding failure, type: " + type.getTypeName() + ", data: " + str, ex);
        }
    }

    @Override
    public void pretreatment(Context ctx) {
        if (ctx.config.getEncoder() instanceof EncoderTyped) {
            ctx.headers.put(ContentTypes.HEADER_SERIALIZATION, ContentTypes.AT_TYPE_JSON);
        }

        ctx.headers.put(ContentTypes.HEADER_ACCEPT, ContentTypes.JSON_VALUE);
    }
}
