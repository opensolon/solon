package org.noear.solon.serialization.jackson;

import org.noear.solon.XApp;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        //XRenderManager.register(render);
        XRenderManager.mapping("@json", new JacksonRender(false));
        XRenderManager.mapping("@type_json", new JacksonRender(true));
    }
}
