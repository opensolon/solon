package org.noear.solon.serialization.protostuff;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AopContext context) {
        ProtostuffRender render = new ProtostuffRender();

        RenderManager.mapping("@protobuf",render);

        //支持 protostuff 内容类型执行
        ProtostuffActionExecutor executor = new ProtostuffActionExecutor();
        EventBus.push(executor);

        Solon.app().chainManager().addExecuteHandler(executor);
    }
}
