package org.noear.solon.serialization.snack3;

import org.noear.solon.XApp;
import org.noear.solon.core.XBridge;
import org.noear.solon.core.XRenderManager;
import org.noear.solon.core.XPlugin;

public class XPluginImp implements XPlugin {
    public static boolean output_meta = false;

    @Override
    public void start(XApp app) {
        output_meta = app.prop().getInt("solon.output.meta", 0) > 0;

        //XRenderManager.register(render);
        XRenderManager.mapping("@json", new SnackRender(false));
        XRenderManager.mapping("@type_json", new SnackRender(true));

        //支持Json内容类型执行
        XBridge.actionExecutorAdd(new SnackJsonActionExecutor());
    }
}
