package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.Utils;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.serialization.ContextSerializer;

import java.io.IOException;

/**
 * @author noear
 * @since 1.5
 * @since 2.8
 */
public class JacksonStringSerializer implements ContextSerializer<String> {
    private static final String label = "/json";

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
    public String getContentType() {
        return "application/json";
    }

    @Override
    public boolean matched(Context ctx, String mime) {
        if (mime == null) {
            return false;
        } else {
            return mime.contains(label);
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
    public void serializeToBody(Context ctx, Object data) throws IOException {
        ctx.contentType(getContentType());

        if (data instanceof ModelAndView) {
            ctx.output(serialize(((ModelAndView) data).model()));
        } else {
            ctx.output(serialize(data));
        }
    }

    @Override
    public Object deserializeFromBody(Context ctx) throws IOException {
        String data = ctx.bodyNew();

        if (Utils.isNotEmpty(data)) {
            return getConfig().readTree(data);
        } else {
            return null;
        }
    }
}