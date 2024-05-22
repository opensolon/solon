package org.noear.solon.serialization.hessian;

import com.alibaba.com.caucho.hessian.io.Hessian2Output;
import org.noear.solon.core.handle.ModelAndView;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.SerializationConfig;

import java.io.ByteArrayOutputStream;

//不要要入参，方便后面多视图混用
//
public class HessianRender implements Render {
    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        if (SerializationConfig.isOutputMeta()) {
            ctx.headerAdd("solon.serialization", "HessianRender");
        }

        ctx.contentType("application/hessian");

        Hessian2Output ho = new Hessian2Output(ctx.outputStream());
        if (obj instanceof ModelAndView) {
            ho.writeObject(((ModelAndView) obj).model());
        } else {
            ho.writeObject(obj);
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