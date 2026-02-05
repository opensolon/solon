package org.noear.solon.shell.core;

import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 命令元数据（封装命令核心信息）
 */
public class CommandMetadata {
    // 命令基本信息
    private String commandName;
    private String commandDescription;

    // 命令执行相关
    private BeanWrap beanWrap;
    private Method targetMethod;
    private List<ParameterMetadata> parameterMetadataList;


    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandDescription() {
        return commandDescription;
    }

    public void setCommandDescription(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    public Object getBeanInstance() {
        return beanWrap.get();
    }

    public void setBeanWrap(BeanWrap beanWrap) {
        this.beanWrap = beanWrap;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {
        this.targetMethod = targetMethod;
    }

    public List<ParameterMetadata> getParameterMetadataList() {
        return parameterMetadataList;
    }

    public void setParameterMetadataList(List<ParameterMetadata> parameterMetadataList) {
        this.parameterMetadataList = parameterMetadataList;
    }

    public static class ParameterMetadata {
        private Class<?> parameterType;
        private String description;
        private String defaultValue;
        private boolean required;

        public Class<?> getParameterType() {
            return parameterType;
        }

        public void setParameterType(Class<?> parameterType) {
            this.parameterType = parameterType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }
    }
}