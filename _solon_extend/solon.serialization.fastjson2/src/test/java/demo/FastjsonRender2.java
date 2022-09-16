package demo;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.Render;
import org.noear.solon.serialization.fastjson2.XPluginImp;

//不要要入参，方便后面多视图混用
//
public class FastjsonRender2 implements Render {
    private boolean _typedJson;

    public FastjsonRender2(boolean typedJson) {
        _typedJson = typedJson;
    }

    @Override
    public void render(Object obj, Context ctx) throws Throwable {
        String txt = null;

        if (_typedJson) {
            //序列化处理
            //
            txt = JSON.toJSONString(obj,
                    JSONWriter.Feature.BrowserCompatible,
                    JSONWriter.Feature.WriteClassName,
                    JSONWriter.Feature.ReferenceDetection);
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
                txt = JSON.toJSONString(obj,
                        JSONWriter.Feature.ReferenceDetection.BrowserCompatible,
                        JSONWriter.Feature.ReferenceDetection.ReferenceDetection);
            }
        }

        if (XPluginImp.output_meta) {
            ctx.headerSet("solon.serialization", "Fastjson2Render");
        }

        ctx.attrSet("output", txt);

        if (obj instanceof String && ctx.accept().contains("/json") == false) {
            ctx.output(txt);
        } else {
            ctx.outputAsJson(txt);
        }
    }
}
