package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.ParameterizedType;

public class SnackJsonActionExecutor extends ActionExecutorDefault {
    private static final String label = "/json";

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.contains(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx) throws Exception {
        return ONode.loadStr(ctx.bodyNew());
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }

        if (ctx.paramMap().containsKey(p.getName())) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
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
                if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                    return super.changeValue(ctx, p, pi, pt, bodyObj);
                } else {
                    return tmp.toObject(pt);
                }
            }
        }

        if (tmp.isArray()) {
            //List<T> 类型转换
            ParameterizedType gp = p.getGenericType();
            if (gp != null) {
                return tmp.toObjectList((Class) gp.getActualTypeArguments()[0]);
            }

            return tmp.toObject(pt);
        }

        return tmp.val().getRaw();
    }
}
