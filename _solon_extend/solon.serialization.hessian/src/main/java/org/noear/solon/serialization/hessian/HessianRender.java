package org.noear.solon.serialization.hessian;

import com.caucho.hessian.io.Hessian2Output;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

//不要要入参，方便后面多视图混用
//
public class HessianRender implements Render {

    @Override
    public String renderAndReturn(Object data, Context ctx) throws Throwable {
        byte[] bytes = null;
        if (data instanceof ModelAndView) {
            bytes = serializeDo(new LinkedHashMap((Map) data));
        } else {
            bytes = serializeDo(data);
        }

        return Base64.getEncoder().encodeToString(bytes);
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "HessianRender");
        }

        ctx.contentType("application/hessian");

        if (obj instanceof ModelAndView) {
            ctx.output(serializeDo(new LinkedHashMap((Map) obj)));
        } else {
            ctx.output(serializeDo(obj));
        }
    }

    private byte[] serializeDo(Object obj) throws Throwable {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Hessian2Output ho = new Hessian2Output(out);
        ho.writeObject(obj);
        ho.close();

        return out.toByteArray();
    }
}
