package org.noear.solon.serialization.protostuff;

import org.noear.solon.core.mvc.ActionExecuteHandlerDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.MethodWrap;
import org.noear.solon.core.wrap.ParamWrap;

import java.util.Map;

/**
 * Protostuff 动作执行器
 *
 * @author noear
 * @since 1.2
 * @since 2.8
 */
public class ProtostuffActionExecutor extends ActionExecuteHandlerDefault {
    private ProtostuffBytesSerializer serializer = new ProtostuffBytesSerializer();

    public ProtostuffBytesSerializer getSerializer() {
        return serializer;
    }

    @Override
    public boolean matched(Context ctx, String ct) {
        return serializer.matched(ctx, ct);
    }

    @Override
    protected Object changeBody(Context ctx, MethodWrap mWrap) throws Exception {
        return serializer.deserializeFromBody(ctx);
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
            if (p.isRequiredBody()) {
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
