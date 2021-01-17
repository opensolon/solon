package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

public class JacksonActionExecutor extends ActionExecutorDefault {
    private static final String label = "/json";

    ObjectMapper mapper = new ObjectMapper();

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
        return mapper.readTree(ctx.body());
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        }

        if (ctx.paramMap().containsKey(p.getName())) {
            return super.changeValue(ctx, p, pi, pt, bodyObj);
        }

        JsonNode tmp = (JsonNode) bodyObj;

        if (tmp.isObject()) {
            if (tmp.has(p.getName())) {
                JsonNode m1 = tmp.get(p.getName());

                return mapper.treeToValue(m1, p.getType());
            } else if (ctx.paramMap().containsKey(p.getName())) {
                //有可能是path变量
                //
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            } else {
                //return tmp.toObject(pt);

                return mapper.treeToValue(tmp, pt);
            }
        }

        if (tmp.isArray()) {
            //return tmp.toObject(pt);
            return mapper.treeToValue(tmp, pt);
        }

        //return tmp.val().getRaw();
        return mapper.treeToValue(tmp, pt);
    }
}
