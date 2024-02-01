package org.noear.solon.command.integration;

import org.noear.solon.command.CommandExecutor;
import org.noear.solon.command.annotation.Command;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.RunUtil;

/**
 * @author noear
 * @since 2.7
 */
public class XPluginImpl implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        CommandManager commandManager = CommandManager.getInstance();

        //注册注解构建器
        context.beanBuilderAdd(Command.class, (clz, bw, anno) -> {
            //构建时，收集命令
            if (bw.raw() instanceof CommandExecutor) {
                commandManager.register(anno.value(), bw.get());
            }
        });

        //订阅事件（启动完成后开始执行）
        context.onEvent(AppLoadEndEvent.class, e -> {
            RunUtil.delay(commandManager::executeAll, 100);
        });
    }
}
