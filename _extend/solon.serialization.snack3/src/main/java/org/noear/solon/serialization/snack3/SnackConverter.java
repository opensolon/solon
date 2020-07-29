package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XConverter;

public class SnackConverter extends XConverter {
    @Override
    public Object convert(XContext ctx, String name, Class<?> type) throws Exception {
        String ct = ctx.contentType();

        if (ct != null && ct.indexOf("/json") > 0) {
            return ONode.deserialize(ctx.body(), type);
        } else {
            return super.convert(ctx, name, type);
        }
    }
}
