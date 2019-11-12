package org.noear.solon.serialization.fastjson;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

//不要要入参，方便后面多视图混用
//
public class FastjsonRender implements XRender {

    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        boolean is_serialize = "serialize".equals(ctx.attr("solon.reader.mode", null));
        String txt = null;

        if(is_serialize){
            //序列化处理
            //
            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.WriteClassName,
                    SerializerFeature.DisableCircularReferenceDetect);
        }else {
            //非序列化处理
            //
            if (obj == null) {
                return;
            }

            if (obj instanceof Throwable) {
                throw (Throwable) obj;
            }

            txt = JSON.toJSONString(obj,
                    SerializerFeature.BrowserCompatible,
                    SerializerFeature.DisableCircularReferenceDetect);
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
