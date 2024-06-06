package org.noear.solon.docs.openapi2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.serialize.Serializer;

import java.io.IOException;

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
    public Object deserialize(String data, Class<?> toClz) throws IOException {
        if (toClz == null) {
            return mapper.readTree(data);
        } else {
            return mapper.readValue(data, toClz);
        }
    }
}
