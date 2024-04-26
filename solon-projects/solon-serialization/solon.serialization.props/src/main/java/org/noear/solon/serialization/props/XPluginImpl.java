package org.noear.solon.serialization.props;

import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.EventBus;

public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) {
        //::actionExecutor
        //支持 props 内容类型执行
        PropsActionExecutor actionExecutor = new PropsActionExecutor();
        context.wrapAndPut(PropsActionExecutor.class, actionExecutor);
        EventBus.publish(actionExecutor);

        Solon.app().chainManager().addExecuteHandler(actionExecutor);
    }
}
