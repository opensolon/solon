package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XConverter;

public class JacksonConverter extends XConverter {
    ObjectMapper mapper = new ObjectMapper();

    @Override
    public Object convert(XContext ctx, String name, Class<?> type) throws Exception {
        String ct = ctx.contentType();

        if (ct != null && ct.indexOf("/json") > 0) {
            return mapper.readValue(ctx.body(), type);
        } else {
            return super.convert(ctx, name, type);
        }
    }
}
