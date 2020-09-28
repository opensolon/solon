package org.noear.solon.serialization.protobuf;

import io.edap.x.protobuf.ProtoBuf;
import org.noear.solon.core.XActionExecutorDefault;
import org.noear.solon.core.XContext;

import java.lang.reflect.Parameter;
import java.util.Map;

public class ProtobufActionExecutor extends XActionExecutorDefault {
    private static final String label = "application/protobuf";

    @Override
    public boolean matched(XContext ctx, String ct) {
        if (ct != null && ct.startsWith(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(XContext ctx) throws Exception {
        return ProtoBuf.der(ctx.bodyAsBytes());
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
