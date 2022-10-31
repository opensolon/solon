package org.noear.solon.serialization.snack3;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {
    public static boolean output_meta = false;

    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        //事件扩展
        EventBus.push(SnackRenderFactory.global);

        RenderManager.mapping("@json", SnackRenderFactory.global.create());
        RenderManager.mapping("@type_json", SnackRenderTypedFactory.global.create());

        //支持Json内容类型执行
        EventBus.push(SnackJsonActionExecutor.global);

        Bridge.actionExecutorAdd(SnackJsonActionExecutor.global);
    }
}
