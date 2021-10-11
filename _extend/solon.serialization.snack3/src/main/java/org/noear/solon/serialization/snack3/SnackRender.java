package org.noear.solon.serialization.snack3;

import org.noear.snack.ONode;
import org.noear.snack.core.Constants;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;

//不要要入参，方便后面多视图混用
//
public class SnackRender implements Render {

    private boolean _typedJson;
    private Constants _config;

    protected SnackRender(boolean typedJson, Constants config) {
        _typedJson = typedJson;
        _config = config;
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (_typedJson) {
            //序列化处理
            //
            //txt = ONode.serialize(obj);
            txt = ONode.load(obj, _config).toJson();
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
                //txt = ONode.stringify(obj);
                txt = ONode.load(obj, _config).toJson();
            }
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "SnackRender");
        }

        ctx.attrSet("output", txt);

        if (obj instanceof String && ctx.accept().contains("/json") == false) {
            ctx.output(txt);
        } else {
            ctx.outputAsJson(txt);
        }
    }
}
