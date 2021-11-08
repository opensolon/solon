package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

import java.lang.reflect.ParameterizedType;

public class FastjsonJsonActionExecutor extends ActionExecutorDefault {
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
        return JSON.parse(ctx.bodyNew());
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }

        if (ctx.paramMap().containsKey(p.getName())) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        if (bodyObj instanceof JSONObject) {
            JSONObject tmp = (JSONObject) bodyObj;

            if (tmp.containsKey(p.getName())) {
                return tmp.getObject(p.getName(), pt);
            } else if (ctx.paramMap().containsKey(p.getName())) {
                //有可能是path变量
                //
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                if (pt.isPrimitive() || pt.getTypeName().startsWith("java.lang.")) {
                    return super.changeValue(ctx, p, pi, pt, bodyObj);
                } else {
                    return tmp.toJavaObject(pt);
                }
            }
        }

        if (bodyObj instanceof JSONArray) {
            JSONArray tmp = (JSONArray) bodyObj;
            //List<T> 类型转换
            ParameterizedType gp = p.getGenericType();
            if (gp != null) {
                return tmp.toJavaList((Class) gp.getActualTypeArguments()[0]);
            }
            return tmp;
        }

        return bodyObj;
    }
}
