package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.XActionExecutor;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class SnackJsonActionExecutor extends XActionExecutor {
    @Override
    public boolean matched(XContext ctx, String contextType) {
        if (contextType != null && contextType.contains("/json")) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(XContext ctx) throws Exception {
        return ONode.loadStr(ctx.body());
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }

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
