package org.noear.solon.serialization.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Input;
import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Map;

public class HessianActionExecutor extends ActionExecuteHandlerDefault {
    private static final String label = "application/hessian";

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.startsWith(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        Hessian2Input hi = new Hessian2Input(ctx.bodyAsStream());
        return hi.readObject();
    }

    /**
     * @since 1.11 增加 requireBody 支持
     * */
    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if(p.isRequiredPath() || p.isRequiredCookie() || p.isRequiredHeader()){
            //如果是 path、cookie, header
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (p.isRequiredBody() == false && ctx.paramMap().containsKey(p.getName())) {
            //有可能是path、queryString变量
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj == null) {
            return null;
        } else {
            if(p.isRequiredBody()){
                return bodyObj;
            }

            if (bodyObj instanceof Map) {
                Map<String, Object> tmp = (Map<String, Object>) bodyObj;

                if (tmp.containsKey(p.getName())) {
                    return tmp.get(p.getName());
                }
            }

            return null;
        }
    }
}
