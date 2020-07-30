package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.noear.solon.core.XActionExecutor;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class FastjsonJsonActionExecutor extends XActionExecutor {

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
        return JSON.parse(ctx.body());
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }

        if (bodyObj instanceof JSONObject) {
            JSONObject tmp = (JSONObject) bodyObj;

            if (tmp.containsKey(p.getName())) {
                return tmp.getObject(p.getName(), pt);
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
