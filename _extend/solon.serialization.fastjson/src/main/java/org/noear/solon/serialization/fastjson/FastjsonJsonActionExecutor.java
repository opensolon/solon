package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.noear.solon.core.XActionExecutorDefault;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class FastjsonJsonActionExecutor extends XActionExecutorDefault {
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
        return JSON.parse(ctx.body());
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, int pi, Class<?> pt, Object bodyObj) throws Exception {
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
                return tmp.toJavaObject(pt);
            }
        }

        if (bodyObj instanceof JSONArray) {
            JSONArray tmp = (JSONArray) bodyObj;
            return tmp.toJavaList(pt);
        }

        return bodyObj;
    }
}
