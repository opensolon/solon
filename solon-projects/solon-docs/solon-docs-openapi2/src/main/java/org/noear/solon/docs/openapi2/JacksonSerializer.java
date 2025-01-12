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
package org.noear.solon.docs.openapi2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.serialize.Serializer;
import org.noear.solon.core.util.ClassUtil;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author noear
 * @since 2.8
 */
public class JacksonSerializer implements Serializer<String> {
    private static final JacksonSerializer instance = new JacksonSerializer();

    public static JacksonSerializer getInstance() {
        return instance;
    }

    private ObjectMapper mapper;

    public JacksonSerializer() {
        mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String name() {
        return "JacksonSerializer";
    }

    @Override
    public String serialize(Object fromObj) throws IOException {
        return mapper.writeValueAsString(fromObj);
    }

    @Override
    public Object deserialize(String data, Type toType) throws IOException {
        if (toType == null) {
            return mapper.readTree(data);
        } else {
            Class<?> clz = ClassUtil.getTypeClass(toType);
            return mapper.readValue(data, clz);
        }
    }
}
