package org.noear.solon.serialization.hessian;

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

        HessianRender render = new HessianRender();

        //RenderManager.register(render);
        RenderManager.mapping("@hessian",render);

        //支持 hessian 内容类型执行
        HessianActionExecutor executor = new HessianActionExecutor();
        EventBus.push(executor);

        Bridge.actionExecutorAdd(executor);
    }
}
