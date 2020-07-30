package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.XActionExecutor;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class SnackJsonActionExecutor extends XActionExecutor {
    private static final String label = "/json";

    @Override
    public boolean matched(XContext ctx, String ct) {
        if (ct != null && ct.contains(label)) {
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
            } else if (ctx.paramMap().containsKey(p.getName())) {
                //有可能是path变量
                //
                return super.changeValue(ctx, p, pi, pt, bodyObj);
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
