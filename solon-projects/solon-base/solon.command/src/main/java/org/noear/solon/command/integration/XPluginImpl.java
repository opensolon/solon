package org.noear.solon.command.integration;

import org.noear.solon.command.annotation.Command;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;

/**
 * @author noear
 * @since 2.7
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CommandBuilder commandBuilder = new CommandBuilder();

        //注册注解构建器
        context.beanBuilderAdd(Command.class, commandBuilder);

        //订阅事件（启动完成后开始执行）
        context.onEvent(AppLoadEndEvent.class, e -> {
            commandBuilder.exec();
        });
    }
}
