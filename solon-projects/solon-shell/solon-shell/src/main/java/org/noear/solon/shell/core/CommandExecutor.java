package org.noear.solon.shell.core;

import org.noear.solon.i18n.I18nUtil;
import org.noear.solon.shell.integration.ShellContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 命令执行器，解析输入的shell命令，反射执行对应的方法（适配 Solon 原生 I18nUtil）
 *
 * @author noear
 * @author shenmk
 * @since 3.9.1
 */
public class CommandExecutor {

    /**
     * 执行命令（核心方法，输入命令行，返回执行结果）
     */
    public static String execute(String commandLine) {
        // 1. 空命令校验（适配 Solon 原生 I18nUtil）
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.empty");
        }

        // 2. 拆分命令名和参数（优化：增加空数组校验）
        String[] commandParts = commandLine.trim().split("\\s+");
        if (commandParts.length == 0) {
            return I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.empty");
        }
        String commandName = commandParts[0];
        String[] inputParams = commandParts.length > 1 ?
                Arrays.copyOfRange(commandParts, 1, commandParts.length) : new String[0];

        // 3. 获取命令元数据（适配 Solon 原生 I18nUtil + 占位符替换）
        Map<String, CommandMetadata> commandRepo = ShellContext.getCommandRepository();
        if (!commandRepo.containsKey(commandName)) {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.not.found");
            return formatMessage(rawMsg, commandName);
        }
        CommandMetadata metadata = commandRepo.get(commandName);

        // 4. 校验必选参数数量（适配 Solon 原生 I18nUtil + 占位符替换）
        List<CommandMetadata.ParameterMetadata> paramMetaList = metadata.getParameterMetadataList();
        long requiredCount = paramMetaList.stream().filter(CommandMetadata.ParameterMetadata::isRequired).count();
        if (inputParams.length < requiredCount) {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.missing.required.params");
            return formatMessage(rawMsg, commandName, requiredCount);
        }

        // 可选：参数数量上限提示（适配 Solon 原生 I18nUtil + 占位符替换）
        if (inputParams.length > paramMetaList.size()) {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.too.many.params");
            return formatMessage(rawMsg, commandName, paramMetaList.size());
        }

        // 5. 转换参数类型，封装方法参数数组
        Object[] methodParams = new Object[paramMetaList.size()];
        for (int i = 0; i < paramMetaList.size(); i++) {
            CommandMetadata.ParameterMetadata paramMeta = paramMetaList.get(i);
            String inputValue = i < inputParams.length ? inputParams[i] : paramMeta.getDefaultValue();

            // 处理默认值
            if ((inputValue == null || inputValue.isEmpty()) && !paramMeta.getDefaultValue().isEmpty()) {
                inputValue = paramMeta.getDefaultValue();
            }

            // 类型转换（适配 Solon 原生 I18nUtil + 占位符替换）
            try {
                methodParams[i] = convertValue(inputValue, paramMeta.getParameterType());
            } catch (Exception e) {
                String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.param.convert.fail");
                return formatMessage(rawMsg, (i + 1), e.getMessage());
            }
        }

        // 6. 反射调用目标方法（适配 Solon 原生 I18nUtil + 占位符替换）
        try {
            Method targetMethod = metadata.getTargetMethod();
            // 可选：参数数量校验（适配 Solon 原生 I18nUtil）
            Class<?>[] paramTypes = targetMethod.getParameterTypes();
            if (paramTypes.length != methodParams.length) {
                return I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.method.param.mismatch");
            }

            targetMethod.setAccessible(true);
            Object result = targetMethod.invoke(metadata.getBeanInstance(), methodParams);
            // 执行成功文案（适配 Solon 原生 I18nUtil）
            return result == null ? I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.execute.success") : result.toString();
        } catch (InvocationTargetException e) {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.execute.fail");
            return formatMessage(rawMsg, e.getTargetException().getMessage());
        } catch (IllegalAccessException e) {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.method.access.fail");
            return formatMessage(rawMsg, e.getMessage());
        }
    }

    /**
     * 补充占位符替换逻辑（适配 Solon 原生 I18nUtil 不支持 {} 替换的问题）
     *
     * @param rawMessage 原始文案（含 {0} {1} 占位符）
     * @param args       替换参数
     * @return 替换后的文案
     */
    private static String formatMessage(String rawMessage, Object... args) {
        if (rawMessage == null || args == null || args.length == 0) {
            return rawMessage;
        }

        String formattedMsg = rawMessage;
        for (int i = 0; i < args.length; i++) {
            String placeholder = "{" + i + "}";
            String replacement = args[i] == null ? "" : args[i].toString();
            formattedMsg = formattedMsg.replace(placeholder, replacement);
        }
        return formattedMsg;
    }

    /**
     * 参数类型转换（仅支持基础类型，适配 Solon 原生 I18nUtil）
     */
    private static Object convertValue(String inputValue, Class<?> targetType) {
        if (inputValue == null || inputValue.isEmpty()) {
            return null;
        }

        if (targetType == String.class) {
            return inputValue;
        } else if (targetType == Integer.class || targetType == int.class) {
            try {
                return Integer.parseInt(inputValue);
            } catch (NumberFormatException e) {
                String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.param.convert.to.int.fail");
                throw new IllegalArgumentException(formatMessage(rawMsg, inputValue));
            }
        } else if (targetType == Long.class || targetType == long.class) {
            try {
                return Long.parseLong(inputValue);
            } catch (NumberFormatException e) {
                String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.param.convert.to.long.fail");
                throw new IllegalArgumentException(formatMessage(rawMsg, inputValue));
            }
        } else {
            String rawMsg = I18nUtil.getMessage(ShellContext.GLOBAL_LOCALE, "shell.command.param.type.not.support");
            throw new IllegalArgumentException(formatMessage(rawMsg, targetType.getName()));
        }
    }
}