package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.XActionConverter;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class SnackConverter extends XActionConverter {
    @Override
    protected Object changeBody(XContext ctx) throws Exception {
        String tmp = ctx.contentType();

        if (tmp != null && tmp.indexOf("/json") > 0) {
            return ONode.loadStr(ctx.body());
        }

        return null;
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return super.changeValue(ctx, p, pt, bodyObj);
        } else {
            ONode tmp = (ONode) bodyObj;

            if (tmp.isObject()) {
                if (tmp.contains(p.getName())) {
                    return tmp.get(p.getName()).toObject(pt);
                } else {
                    return tmp.toObject(pt);
                }
            }

            if (tmp.isArray()) {
                return tmp.toObject(pt);
            }

            return tmp.val().getRaw();
        }
    }
}
