package org.noear.solon.shell.integration;

import org.noear.solon.annotation.Param;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.core.event.AppLoadEndEvent;
import org.noear.solon.shell.annotation.Command;
import org.noear.solon.shell.core.CommandMetadata;
import org.noear.solon.shell.core.SolonShell;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * 命令扫描器（复用 Solon Plugin 机制，随 Solon 启动执行）
 */
public class ShellPlugin implements Plugin {
    protected static final Map<String, CommandMetadata> COMMAND_REPOSITORY = new HashMap<>();

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
            SolonShell.start();
        });
    }

    private void buildMethodCommand(BeanWrap beanWrap, Method method, Command anno) {
        String commandName = anno.value();
        if (COMMAND_REPOSITORY.containsKey(commandName)) {
            throw new IllegalArgumentException("Repeat command name: " + commandName);
        }

        CommandMetadata metadata = new CommandMetadata();
        metadata.setCommandName(commandName);
        metadata.setCommandDescription(anno.description());
        metadata.setBeanWrap(beanWrap);
        metadata.setTargetMethod(method);
        metadata.setParameterMetadataList(parseParameters(method.getParameters()));

        COMMAND_REPOSITORY.put(commandName, metadata);
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

    public static Map<String, CommandMetadata> getCommandRepository() {
        return Collections.unmodifiableMap(COMMAND_REPOSITORY);
    }
}