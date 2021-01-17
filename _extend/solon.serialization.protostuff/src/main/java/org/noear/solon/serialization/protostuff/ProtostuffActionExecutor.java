package org.noear.solon.serialization.protostuff;

import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.noear.solon.core.handle.ActionExecutorDefault;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.wrap.ParamWrap;

/**
 * @author noear 2021/1/17 created
 */
public class ProtostuffActionExecutor extends ActionExecutorDefault {
    private static final String label = "application/protobuf";

    @Override
    public boolean matched(Context ctx, String ct) {
        if (ct != null && ct.startsWith(label)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected Object changeBody(Context ctx) throws Exception {
        return ctx.bodyAsBytes();
    }

    @Override
    protected Object changeValue(Context ctx, ParamWrap p, int pi, Class<?> pt, Object bodyObj) throws Exception {
        if (bodyObj == null) {
            return null;
        } else {
            if (ctx.paramMap().containsKey(p.getName())) {
                //有可能是path变量
                //
                return super.changeValue(ctx, p, pi, pt, bodyObj);
            }else{
                Object obj = ProtostuffUtil.deserializer((byte[]) bodyObj, pt);
                return obj;
            }
        }
    }
}
