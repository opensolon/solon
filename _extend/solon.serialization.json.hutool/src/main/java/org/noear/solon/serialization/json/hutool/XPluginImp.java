package org.noear.solon.serialization.json.hutool;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.handle.RenderManager;

/**
 * @author noear
 * @since 1.5
 */
public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        RenderManager.mapping("@json", new HutoolJsonRender(false));
        RenderManager.mapping("@type_json", new HutoolJsonRender(true));

        //支持Json内容类型执行
        Bridge.actionExecutorAdd(new HutoolJsonActionExecutor());
    }
}