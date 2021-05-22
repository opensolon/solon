package org.noear.solon.serialization.snack3;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        RenderManager.mapping("@json", new SnackRender(false));
        RenderManager.mapping("@type_json", new SnackRender(true));

        //支持Json内容类型执行
        Bridge.actionExecutorAdd(new SnackJsonActionExecutor());
    }
}
