package org.noear.solon.serialization.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XRender;

public class JacksonRender implements XRender {
    @Override
    public void render(Object obj, XContext ctx) throws Throwable {
        boolean is_serialize = "serialize".equals(ctx.attr("solon.reader.mode", null));

        String txt = null;

        if (is_serialize) {
            //序列化处理
            //
            ObjectMapper mapper = new ObjectMapper();
            txt = mapper.writeValueAsString(obj);
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

            ObjectMapper mapper = new ObjectMapper();
            txt = mapper.writeValueAsString(obj);
        }

        ctx.attrSet("output", txt);
        ctx.outputAsJson(txt);
    }
}
