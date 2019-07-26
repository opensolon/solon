package org.noear.solon.view.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

//不要要入参，方便后面多视图混用
//
public class FastjsonRender implements XRender {

    @Override
    public void render(Object obj, XContext ctx) throws Exception {
        boolean is_rpc = "service".equals(ctx.attr("solon.reader.source",null ));

        if(is_rpc == false){
            if(obj instanceof String){
                ctx.output((String) obj);
                return;
            }

            if(obj instanceof Exception){
                throw (Exception) obj;
            }
        }

        String txt = null;
        if (is_rpc) {
            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.WriteClassName,
                    SerializerFeature.DisableCircularReferenceDetect);
        } else {
            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.DisableCircularReferenceDetect);
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
