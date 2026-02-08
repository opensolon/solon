/*
 * Copyright 2017-2026 noear.org and authors
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
package org.noear.solon.shell.integration;

import org.noear.solon.annotation.Param;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.shell.annotation.Command;
import org.noear.solon.shell.core.CommandMetadata;
import org.noear.solon.shell.core.ShellReplJansi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

/**
 * 命令扫描器（复用 Solon Plugin 机制，随 Solon 启动执行）
 *
 * @author noear
 * @author shenmk
 * @since 3.9.1
 */
public class ShellPluginJansi implements Plugin {
    private static final Logger log = LoggerFactory.getLogger(ShellPluginJansi.class);

    @Override
    public void start(AppContext context) {
        //注册提取器
        context.beanExtractorAdd(Command.class, (bw, method, anno) -> {
            buildMethodCommand(bw, method, anno);
        });

        //生成默认命令
        context.beanMake(HelpCommand.class);

        //设定启动时机
        context.onEvent(AppLoadEndEvent.class, e -> {
            ShellReplJansi.start();
        });
    }

    private void buildMethodCommand(BeanWrap beanWrap, Method method, Command anno) {
        String commandName = anno.value();
        if (ShellContext.COMMAND_REPOSITORY.containsKey(commandName)) {
            throw new IllegalArgumentException("Repeat command name: " + commandName);
        }
        log.info("registry command name: {}, bean {}", commandName, beanWrap.clz().getName());
        CommandMetadata metadata = new CommandMetadata();
        metadata.setCommandName(commandName);
        metadata.setCommandDescription(anno.description());
        metadata.setBeanWrap(beanWrap);
        metadata.setTargetMethod(method);
        metadata.setParameterMetadataList(parseParameters(method.getParameters()));

        ShellContext.COMMAND_REPOSITORY.put(commandName, metadata);
    }

    private List<CommandMetadata.ParameterMetadata> parseParameters(Parameter[] parameters) {
        List<CommandMetadata.ParameterMetadata> paramList = new ArrayList<>();

        for (Parameter p1 : parameters) {
            CommandMetadata.ParameterMetadata metadata = new CommandMetadata.ParameterMetadata();
            metadata.setParameterType(p1.getType());

            if (p1.isAnnotationPresent(Param.class)) {
                Param option = p1.getAnnotation(Param.class);
                metadata.setDescription(option.description());
                metadata.setDefaultValue(option.defaultValue());
                metadata.setRequired(option.required());
            } else {
                metadata.setDescription("");
                metadata.setDefaultValue("");
                metadata.setRequired(false);
            }

            paramList.add(metadata);
        }

        return paramList;
    }

}