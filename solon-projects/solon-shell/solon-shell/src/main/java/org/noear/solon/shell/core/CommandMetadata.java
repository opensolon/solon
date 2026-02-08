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
package org.noear.solon.shell.core;

import org.noear.solon.core.BeanWrap;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 命令元数据（封装命令核心信息）
 *
 * @author noear
 * @author shenmk
 * @since 3.9.1
 */
public class CommandMetadata {
    // 命令基本信息，命令名称、描述
    private String commandName;
    private String commandDescription;

    // 命令执行相关，对应的类、方法、参数
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