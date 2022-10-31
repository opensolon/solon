package org.noear.solon.serialization.jackson;

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
        EventBus.push(JacksonRenderFactory.global);

        RenderManager.mapping("@json", JacksonRenderFactory.global.create());
        RenderManager.mapping("@type_json", JacksonRenderTypedFactory.global.create());

        //支持Json内容类型执行
        EventBus.push(JacksonActionExecutor.global);

        Bridge.actionExecutorAdd(JacksonActionExecutor.global);
    }
}
