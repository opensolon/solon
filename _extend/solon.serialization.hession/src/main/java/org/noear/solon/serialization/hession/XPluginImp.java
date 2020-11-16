package org.noear.solon.serialization.hession;

import org.noear.solon.Solon;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(Solon app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        HessionRender render = new HessionRender();

        //XRenderManager.register(render);
        Bridge.renderMapping("@hession",render);
        Bridge.actionExecutorAdd(new HessianActionExecutor());
    }
}
