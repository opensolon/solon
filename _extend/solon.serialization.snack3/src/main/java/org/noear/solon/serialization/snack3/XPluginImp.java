package org.noear.solon.serialization.snack3;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        Bridge.renderMapping("@json", new SnackRender(false));
        Bridge.renderMapping("@type_json", new SnackRender(true));

        //支持Json内容类型执行
        Bridge.actionExecutorAdd(new SnackJsonActionExecutor());
    }
}
