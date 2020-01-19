package org.noear.solon.serialization.jsoniter;

import com.jsoniter.output.JsonStream;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

//不要要入参，方便后面多视图混用
//
public class JsoniterRender implements XRender {

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        String txt = null;

        if (ctx.remoting()) {
            //序列化处理
            //
            txt = JsonStream.serialize(obj);
        } else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            if (obj instanceof String) {
                ctx.output((String) obj); //不能做为json输出
                return;
            }

            txt = JsonStream.serialize(obj);
        }

        if(XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "SnackRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
