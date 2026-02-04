package org.noear.solon.shell.integration;

import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.Plugin;
import org.noear.solon.shell.annotation.Command;
import org.noear.solon.shell.annotation.Option;
import org.noear.solon.shell.core.CommandMetadata;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令扫描器（复用 Solon Plugin 机制，随 Solon 启动执行）
 */
public class ShellPlugin implements Plugin {
    // 全局命令仓库：key=命令名，value=命令元数据（线程安全，用 HashMap）
    private static final Map<String, CommandMetadata> COMMAND_REPOSITORY = new HashMap<>();

    @Override
    public void start(AppContext context) {
        // 1. 扫描 Solon 容器中所有 @Component 标记的 Bean（复用 Solon 扫描结果）
        context.subWrapsOfType(Object.class, bw -> {
            scanCommandMethods(bw);
        });
        // 2. 注册内置 help 命令（无需额外类，直接添加）
        registerBuiltInHelpCommand();

        //todo 启动时机
        //SolonShell.start();
    }

    /**
     * 扫描单个 Bean 中的 @Command 方法，封装元数据存入仓库
     */
    private void scanCommandMethods(BeanWrap beanWrap) {
        Class<?> beanClass = beanWrap.clz();
        Object beanInstance = beanWrap.raw();

        // 遍历 Bean 中所有方法，筛选带 @Command 注解的方法
        for (Method method : beanClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                String commandName = command.value();
                // 校验命令名唯一性（重复直接抛异常）
                if (COMMAND_REPOSITORY.containsKey(commandName)) {
                    throw new IllegalArgumentException("【Solon Shell】重复命令名：" + commandName);
                }
                // 3. 封装命令元数据
                CommandMetadata metadata = new CommandMetadata();
                metadata.setCommandName(commandName);
                metadata.setCommandDescription(command.description());
                metadata.setBeanInstance(beanInstance);
                metadata.setTargetMethod(method);
                metadata.setParameterMetadataList(parseParameters(method.getParameters()));

                // 4. 存入命令仓库
                COMMAND_REPOSITORY.put(commandName, metadata);
            }
        }
    }

    /**
     * 解析方法参数，封装为 ParameterMetadata（仅处理 @Option 注解）
     */
    private List<CommandMetadata.ParameterMetadata> parseParameters(Parameter[] parameters) {
        List<CommandMetadata.ParameterMetadata> paramList = new ArrayList<>();
        for (Parameter param : parameters) {
            CommandMetadata.ParameterMetadata paramMetadata = new CommandMetadata.ParameterMetadata();
            paramMetadata.setParameterType(param.getType());

            // 解析 @Option 注解，无注解则使用默认值
            if (param.isAnnotationPresent(Option.class)) {
                Option option = param.getAnnotation(Option.class);
                paramMetadata.setDescription(option.description());
                paramMetadata.setDefaultValue(option.defaultValue());
                paramMetadata.setRequired(option.required());
            } else {
                paramMetadata.setDescription("无描述");
                paramMetadata.setDefaultValue("");
                paramMetadata.setRequired(false);
            }

            paramList.add(paramMetadata);
        }
        return paramList;
    }

    /**
     * 注册内置 help 命令（展示所有命令列表）
     */
    private void registerBuiltInHelpCommand() {
        CommandMetadata helpMetadata = new CommandMetadata();
        helpMetadata.setCommandName("help");
        helpMetadata.setCommandDescription("展示所有可用命令");
        helpMetadata.setBeanInstance(this);
        Method helpCommand = null;
        try {
            helpCommand = getClass().getDeclaredMethod("helpCommand");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        helpMetadata.setTargetMethod(helpCommand);
        helpMetadata.setParameterMetadataList(new ArrayList<>());

        COMMAND_REPOSITORY.put("help", helpMetadata);
    }

    /**
     * 内置 help 命令实现（打印所有命令）
     */
    public String helpCommand() {
        StringBuilder sb = new StringBuilder();
        sb.append("=====================================\n");
        sb.append("Solon Shell  - 可用命令列表\n");
        sb.append("=====================================\n");
        COMMAND_REPOSITORY.forEach((cmdName, cmdMeta) -> {
            sb.append(cmdName).append(" - ").append(cmdMeta.getCommandDescription()).append("\n");
        });
        sb.append("=====================================\n");
        sb.append("输入 'exit' 退出终端\n");
        return sb.toString();
    }

    /**
     * 对外提供命令仓库访问（返回不可修改副本，直接返回原 Map）
     */
    public static Map<String, CommandMetadata> getCommandRepository() {
        return new HashMap<>(COMMAND_REPOSITORY);
    }
}