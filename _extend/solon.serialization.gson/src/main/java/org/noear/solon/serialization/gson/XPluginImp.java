package org.noear.solon.serialization.gson;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        XBridge.renderMapping("@json", new GsonRender(false));
        XBridge.renderMapping("@type_json", new GsonRender(true));
    }
}
