package org.noear.solon.serialization.hession;

import com.caucho.hessian.io.Hessian2Output;
import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

//不要要入参，方便后面多视图混用
//
public class HessionRender implements XRender {

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "HessionRender");
        }

        ctx.contentType("x-application/hessian");

        if (obj instanceof ModelAndView) {
            writeDo(ctx.outputStream(), new LinkedHashMap((Map) obj));
        } else {
            writeDo(ctx.outputStream(), obj);
        }
    }

    private void writeDo(OutputStream out, Object obj) throws Throwable {
        Hessian2Output ho = new Hessian2Output(out);

        ho.writeObject(obj);
        ho.getBytesOutputStream().flush();
        ho.completeMessage();
        ho.close();
    }
}
