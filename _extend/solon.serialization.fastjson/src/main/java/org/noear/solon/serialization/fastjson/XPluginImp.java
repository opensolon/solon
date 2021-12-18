package org.noear.solon.serialization.fastjson;

import org.noear.solon.SolonApp;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(SolonApp app) {
        output_meta = app.cfg().getInt("solon.output.meta", 0) > 0;

        //事件扩展
        EventBus.push(FastjsonRenderFactory.global);

        RenderManager.mapping("@json", FastjsonRenderFactory.global.create());
        RenderManager.mapping("@type_json",FastjsonRenderTypedFactory.global.create());

        //支持Json内容类型执行
        Bridge.actionExecutorAdd(new FastjsonJsonActionExecutor());
    }
}
