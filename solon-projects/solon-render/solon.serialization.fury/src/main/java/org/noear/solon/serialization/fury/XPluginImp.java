package org.noear.solon.serialization.fury;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImp implements Plugin {

    @Override
    public void start(AppContext context) {
        FuryRender render = new FuryRender();

        //RenderManager.register(render);
        RenderManager.mapping("@fury",render);

        //支持 hessian 内容类型执行
        FuryActionExecutor executor = new FuryActionExecutor();
        EventBus.publish(executor);

        Solon.app().chainManager().addExecuteHandler(executor);
    }
}
