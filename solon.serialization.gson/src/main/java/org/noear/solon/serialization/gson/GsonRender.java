package org.noear.solon.serialization.gson;

import com.google.gson.Gson;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

public class GsonRender implements XRender {
    Gson mapper = new Gson();

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        boolean is_serialize = "serialize".equals(ctx.attr("solon.reader.mode", null));

        String txt = null;

        if (is_serialize) {
            //序列化处理
            //
            txt = mapper.toJson(obj);
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

            txt = mapper.toJson(obj);
        }

        if(XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "GsonRender");
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
