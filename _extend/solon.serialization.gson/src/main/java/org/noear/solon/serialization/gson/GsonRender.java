package org.noear.solon.serialization.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

public class GsonRender implements Render {
    Gson stringify = new GsonBuilder()
            .registerTypeAdapter(java.util.Date.class, new GsonDateSerialize())
            .create();//json输出

    Gson serialize = new GsonBuilder()
            .registerTypeAdapter(java.util.Date.class, new GsonDateSerialize())
            .create();//序列化输出

    private boolean _typedJson;

    public GsonRender(boolean typedJson) {
        _typedJson = typedJson;
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (_typedJson) {
            //序列化处理
            //
            txt = serialize.toJson(obj, obj.getClass());
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
                txt = (String) obj;
            } else {
                txt = stringify.toJson(obj);
            }
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "GsonRender");
        }

        ctx.attrSet("output", txt);

        if (obj instanceof String && ctx.accept().contains("/json") == false) {
            ctx.output(txt);
        } else {
            ctx.outputAsJson(txt);
        }
    }
}
