package org.noear.solon.serialization.hession;

import org.noear.solon.core.ModelAndView;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

//不要要入参，方便后面多视图混用
//
public class HessionRender implements XRender {

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        if(XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "HessionRender");
        }
        byte[] bytes = null;

        if(obj instanceof ModelAndView){
            //bytes = ProtoBuf.toByteArray(new HashMap<>(((Map)obj)));
        }else{
            //bytes = ProtoBuf.toByteArray(obj);
        }

        ctx.attrSet("output", bytes);
        ctx.output(new ByteArrayInputStream(bytes));
    }
}
