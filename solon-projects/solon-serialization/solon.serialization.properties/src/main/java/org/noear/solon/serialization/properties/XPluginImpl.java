package org.noear.solon.serialization.properties;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.LifecycleIndex;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.handle.RenderManager;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        //::renderFactory
        //绑定属性
        PropertiesRenderFactory renderFactory = new PropertiesRenderFactory();

        //事件扩展
        context.wrapAndPut(PropertiesRenderFactory.class, renderFactory);
        EventBus.publish(renderFactory);

        context.lifecycle(LifecycleIndex.PLUGIN_BEAN_BUILD, () -> {
            //晚点加载，给定制更多时机
            RenderManager.mapping("@properties", renderFactory.create());
        });

        //::actionExecutor
        //支持 props 内容类型执行
        PropertiesActionExecutor actionExecutor = new PropertiesActionExecutor();
        context.wrapAndPut(PropertiesActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
