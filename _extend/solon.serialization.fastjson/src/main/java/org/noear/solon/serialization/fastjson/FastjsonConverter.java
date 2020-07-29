package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XConverter;

public class FastjsonConverter extends XConverter {
    @Override
    public Object convert(XContext ctx, Class<?> clz) throws Exception {
        String ct = ctx.contentType();

        if (ct != null && ct.indexOf("/json") > 0) {
            return JSON.parseObject(ctx.body(), clz);
        } else {
            return super.convert(ctx, clz);
        }
    }
}
