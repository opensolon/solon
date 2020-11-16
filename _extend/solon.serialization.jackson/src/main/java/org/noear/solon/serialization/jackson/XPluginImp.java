package org.noear.solon.serialization.jackson;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        //XBridge.renderRegister(render);
        Bridge.renderMapping("@json", new JacksonRender(false));
        Bridge.renderMapping("@type_json", new JacksonRender(true));
    }
}
