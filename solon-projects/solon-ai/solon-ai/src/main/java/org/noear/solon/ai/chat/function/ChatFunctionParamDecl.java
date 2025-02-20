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
package org.noear.solon.ai.chat.function;

/**
 * 聊天函数的参数申明
 *
 * @author noear
 * @since 3.1
 */
public class ChatFunctionParamDecl implements ChatFunctionParam {
    private final String name;
    private final Class<?> type;
    private final boolean required;
    private final String description;

    public ChatFunctionParamDecl(String name, Class<?> type, String description) {
        this(name, type, false, description);
    }

    public ChatFunctionParamDecl(String name, Class<?> type, boolean required, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.description = description;
    }

    /**
     * 参数名字
     */
    @Override
    public String name() {
        return name;
    }

    /**
     * 参数类型
     */
    @Override
    public Class<?> type() {
        return type;
    }

    /**
     * 参数描述
     */
    @Override
    public String description() {
        return description;
    }

    /**
     * 是否必须
     */
    @Override
    public boolean required() {
        return required;
    }
}