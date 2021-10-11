package org.noear.solon.serialization.gson;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;


        RenderManager.mapping("@json", GsonRenderFactory.global.create());
        RenderManager.mapping("@type_json", GsonRenderTypedFactory.global.create());
    }
}
