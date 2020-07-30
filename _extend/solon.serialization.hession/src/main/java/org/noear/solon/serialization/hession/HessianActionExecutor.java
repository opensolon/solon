package org.noear.solon.serialization.hession;

import com.caucho.hessian.io.Hessian2Input;
import org.noear.solon.core.XActionExecutor;
import org.noear.solon.core.XContext;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Parameter;
import java.util.Map;

public class HessianActionExecutor extends XActionExecutor {
    private static final String label = "application/hessian";

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
        Hessian2Input hi = new Hessian2Input(new ByteArrayInputStream(ctx.bodyAsBytes()));
        return hi.readObject();
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, int pi, Class<?> pt, Object bodyObj) throws Exception {
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
