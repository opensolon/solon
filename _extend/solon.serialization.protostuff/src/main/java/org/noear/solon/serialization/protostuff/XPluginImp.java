package org.noear.solon.serialization.protostuff;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;

/**
 * @author noear 2021/1/17 created
 */
public class XPluginImp implements Plugin {
    public static boolean output_meta = false;
    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        ProtostuffRender render = new ProtostuffRender();

        Bridge.renderMapping("@protobuf",render);
        Bridge.actionExecutorAdd(new ProtostuffActionExecutor());
    }
}
