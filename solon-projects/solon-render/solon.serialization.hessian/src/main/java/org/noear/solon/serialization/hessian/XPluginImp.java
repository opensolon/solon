package org.noear.solon.serialization.hessian;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        HessianRender render = new HessianRender();

        //RenderManager.register(render);
        RenderManager.mapping("@hessian",render);

        //支持 hessian 内容类型执行
        HessianActionExecutor executor = new HessianActionExecutor();
        EventBus.publish(executor);

        Solon.app().chainManager().addExecuteHandler(executor);
    }
}
