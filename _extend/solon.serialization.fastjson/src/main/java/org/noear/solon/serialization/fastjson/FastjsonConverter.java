package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.noear.solon.core.XActionConverter;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;

public class FastjsonConverter extends XActionConverter {
    @Override
    protected Object changeBody(XContext ctx) throws Exception {
        String tmp = ctx.contentType();
        if (tmp != null && tmp.indexOf("/json") > 0) {
            return JSON.parse(ctx.body());
        }

        return null;
    }

    @Override
    protected Object changeValue(XContext ctx, Parameter p, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return super.changeValue(ctx, p, pt, bodyObj);
        } else {
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

    @Override
    protected Object changeEntity(XContext ctx, String name, Class<?> type, Object bodyObj) throws Exception {
        String tmp = ctx.contentType();
        if (tmp != null && tmp.indexOf("/json") > 0) {
            return JSON.parseObject(ctx.body(), type);
        } else {
            return super.changeEntity(ctx, name, type, bodyObj);
        }
    }
}
