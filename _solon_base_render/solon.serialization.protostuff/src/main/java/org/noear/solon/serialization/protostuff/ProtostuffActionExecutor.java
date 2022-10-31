package org.noear.solon.serialization.protostuff;

import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public class ProtostuffActionExecutor extends ActionExecutorDefault {
    private static final String label = "application/protobuf";

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.startsWith(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx) throws Exception {
        return ProtostuffUtil.deserialize(ctx.bodyAsBytes());
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        } else {
            if (bodyObj instanceof Map) {
                Map<String, Object> tmp = (Map<String, Object>) bodyObj;

                if (tmp.containsKey(p.getName())) {
                    return tmp.get(p.getName());
                } else if (ctx.paramMap().containsKey(p.getName())) {
                    //有可能是path变量
                    //
                    return super.changeValue(ctx, p, pi, pt, bodyObj);
                }
            }

            return null;
        }
    }
}
