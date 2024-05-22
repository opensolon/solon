package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.serialization.ActionSerializer;

import java.io.IOException;

/**
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class JacksonStringSerializer implements ActionSerializer<String> {
    private ObjectMapper config;

    public ObjectMapper getConfig() {
        if (config == null) {
            config = new ObjectMapper();
        }

        return config;
    }

    public void setConfig(ObjectMapper config) {
        if (config != null) {
            this.config = config;
        }
    }

    @Override
    public String name() {
        return "jackson-json";
    }

    @Override
    public String serialize(Object obj) throws IOException {
        return getConfig().writeValueAsString(obj);
    }

    @Override
    public Object deserialize(String data, Class<?> clz) throws IOException {
        if (clz == null) {
            return getConfig().readTree(data);
        } else {
            return getConfig().readValue(data, clz);
        }
    }

    @Override
    public Object deserializeBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getConfig().readTree(data);
        } else {
            return null;
        }
    }
}