package org.noear.solon.serialization.protostuff;

import org.noear.solon.Solon;
import org.noear.solon.core.AopContext;
import org.noear.solon.core.Bridge;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

/**
 * @author noear
 * @since 1.2
 */
public class XPluginImp implements Plugin {
    public static boolean output_meta = false;
    @Override
    public void start(AopContext context) {
        output_meta = Solon.cfg().getInt("solon.output.meta", 0) > 0;

        ProtostuffRender render = new ProtostuffRender();

        RenderManager.mapping("@protobuf",render);

        //支持 protostuff 内容类型执行
        ProtostuffActionExecutor executor = new ProtostuffActionExecutor();
        EventBus.push(executor);

        Bridge.actionExecutorAdd(executor);
    }
}
