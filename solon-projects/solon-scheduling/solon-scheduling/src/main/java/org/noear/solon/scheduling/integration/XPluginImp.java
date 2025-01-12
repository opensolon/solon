/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.scheduling.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.scheduling.annotation.*;
import org.noear.solon.scheduling.async.AsyncInterceptor;
import org.noear.solon.scheduling.command.CommandExecutor;
import org.noear.solon.scheduling.command.CommandExecutorProxy;
import org.noear.solon.scheduling.command.CommandManager;
import org.noear.solon.scheduling.retry.RetryInterceptor;

import java.lang.annotation.Annotation;

/**
 * @author noear
 * @since 2.2
 */
public class XPluginImp implements Plugin {
    @Override
    public void start(AppContext context) throws Throwable {
        Class<?> source = context.app().source();

        // @since 2.2
        Annotation enableAnno = source.getAnnotation(EnableAsync.class);
        if (enableAnno != null) {
            context.beanInterceptorAdd(Async.class, new AsyncInterceptor(context));
        }

        // @since 2.3
        Annotation enableRetryAnno = source.getAnnotation(EnableRetry.class);
        if (enableRetryAnno != null) {
            //超大，越里面！
            context.beanInterceptorAdd(Retry.class, new RetryInterceptor(context), Integer.MAX_VALUE);
        }

        //允许在外部手动构建，但是可能不会被启用
        CommandManager commandManager = CommandManager.getInstance();
        Annotation enableCommandAnno = source.getAnnotation(EnableCommand.class);
        if (enableCommandAnno != null) {
            //注册注解构建器
            context.beanBuilderAdd(Command.class, (clz, bw, anno) -> {
                //构建时，收集命令
                if (bw.raw() instanceof CommandExecutor) {
                    CommandExecutor executor = new CommandExecutorProxy(bw);
                    commandManager.register(anno.value(), executor);
                }
            });

            //订阅事件（启动完成后开始执行）
            context.onEvent(AppLoadEndEvent.class, e -> {
                RunUtil.delay(commandManager::executeAll, 100);
            });
        }
    }
}
