package org.noear.solon.shell.core;

import org.noear.solon.shell.integration.ShellPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 命令执行器（仅实现核心执行逻辑）
 */
public class CommandExecutor {
    /**
     * 执行命令（核心方法，输入命令行，返回执行结果）
     */
    public static String execute(String commandLine) {
        if (commandLine == null || commandLine.trim().isEmpty()) {
            return "命令不能为空，请输入 'help' 查看可用命令";
        }

        // 1. 拆分命令名和参数（按空格拆分，不支持引号包裹）
        String[] commandParts = commandLine.trim().split("\\s+");
        String commandName = commandParts[0];
        String[] inputParams = commandParts.length > 1 ? 
                Arrays.copyOfRange(commandParts, 1, commandParts.length) : new String[0];

        // 2. 获取命令元数据
        Map<String, CommandMetadata> commandRepo = ShellPlugin.getCommandRepository();
        if (!commandRepo.containsKey(commandName)) {
            return "【错误】命令不存在：" + commandName + "（输入 'help' 查看可用命令）";
        }
        CommandMetadata metadata = commandRepo.get(commandName);

        // 3. 校验必选参数数量
        List<CommandMetadata.ParameterMetadata> paramMetaList = metadata.getParameterMetadataList();
        long requiredCount = paramMetaList.stream().filter(CommandMetadata.ParameterMetadata::isRequired).count();
        if (inputParams.length < requiredCount) {
            return "【错误】命令 '" + commandName + "' 缺少必选参数，需传入 " + requiredCount + " 个必选参数";
        }

        // 4. 转换参数类型，封装方法参数数组
        Object[] methodParams = new Object[paramMetaList.size()];
        for (int i = 0; i < paramMetaList.size(); i++) {
            CommandMetadata.ParameterMetadata paramMeta = paramMetaList.get(i);
            String inputValue = i < inputParams.length ? inputParams[i] : paramMeta.getDefaultValue();

            // 处理默认值（无输入且有默认值时使用默认值）
            if ((inputValue == null || inputValue.isEmpty()) && !paramMeta.getDefaultValue().isEmpty()) {
                inputValue = paramMeta.getDefaultValue();
            }
            // 5. 基础类型转换（仅支持 String/Integer/Long）
            try {
                methodParams[i] = convertValue(inputValue, paramMeta.getParameterType());
            } catch (Exception e) {
                return "【错误】参数 " + (i+1) + " 类型转换失败：" + e.getMessage();
            }
        }
        // 6. 反射调用目标方法，执行命令
        try {
            Method targetMethod = metadata.getTargetMethod();
            targetMethod.setAccessible(true); // 允许访问私有方法（方便用户使用）
            Object result = targetMethod.invoke(metadata.getBeanInstance(), methodParams);
            // 处理返回结果（转换为 String）
            return result == null ? "命令执行成功" : result.toString();
        } catch (InvocationTargetException e) {
            return "【错误】命令执行失败：" + e.getTargetException().getMessage();
        } catch (IllegalAccessException e) {
            return "【错误】无法访问命令方法：" + e.getMessage();
        }
    }

    /**
     * 参数类型转换（仅支持基础类型，后续可扩展）
     */
    private static Object convertValue(String inputValue, Class<?> targetType) {
        if (inputValue == null || inputValue.isEmpty()) {
            return null;
        }

        if (targetType == String.class) {
            return inputValue;
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.parseInt(inputValue);
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.parseLong(inputValue);
        } else {
            throw new IllegalArgumentException("不支持的参数类型：" + targetType.getName());
        }
    }
}