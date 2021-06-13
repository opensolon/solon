package org.noear.solon.serialization.json.hutool;

import cn.hutool.json.JSONUtil;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

/**
 * @author noear
 * @since 1.5
 */
public class HutoolJsonRender implements Render {

    private boolean _typedJson;

    public HutoolJsonRender(boolean typedJson) {
        _typedJson = typedJson;
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (_typedJson) {
            //序列化处理
            //
            txt = JSONUtil.toJsonStr(obj);
        } else if (ctx.accept().indexOf("/json") > 0) {
            txt = JSONUtil.toJsonStr(obj);
        } else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            if (obj instanceof String && ctx.accept().indexOf("/json") < 0) {
                ctx.output((String) obj); //不能做为json输出
                return;
            }

            txt = JSONUtil.toJsonStr(obj);
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "HutoolJsonRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
