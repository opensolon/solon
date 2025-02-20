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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 聊天函数申明（相当于构建器）
 *
 * @author noear
 * @since 3.1
 */
public class ChatFunctionDecl implements ChatFunction {
    private final String name;
    private final List<ChatFunctionParam> params;
    private String description;
    private Function<Map<String, Object>, String> handler;

    /**
     * @param name 函数名字
     */
    public ChatFunctionDecl(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    /**
     * 申明函数描述
     *
     * @param description 参数
     */
    public ChatFunctionDecl description(String description) {
        this.description = description;
        return this;
    }

    /**
     * 申明函数参数
     *
     * @param name        参数名字
     * @param type        参数类型
     * @param description 参数描述
     */
    public ChatFunctionDecl param(String name, Class<?> type, String description) {
        if (type.isPrimitive() == false) {
            throw new IllegalArgumentException("type must be primitive");
        }

        params.add(new ChatFunctionParamDecl(name, type, description));
        return this;
    }

    /**
     * 申明函数字符串参数
     *
     * @param name        参数名字
     * @param description 参数描述
     */
    public ChatFunctionDecl stringParam(String name, String description) {
        return param(name, String.class, description);
    }

    /**
     * 申明函数整型参数
     *
     * @param name        参数名字
     * @param description 参数描述
     */
    public ChatFunctionDecl intParam(String name, String description) {
        return param(name, int.class, description);
    }

    /**
     * 申明函数浮点数参数
     *
     * @param name        参数名字
     * @param description 参数描述
     */
    public ChatFunctionDecl floatParam(String name, String description) {
        return param(name, float.class, description);
    }

    /**
     * 申明函数处理
     *
     * @param handler 处理器
     */
    public ChatFunctionDecl handle(Function<Map<String, Object>, String> handler) {
        this.handler = handler;
        return this;
    }

    /// /////////////////////

    /**
     * 函数名字
     */
    @Override
    public String name() {
        return name;
    }


    /**
     * 函数描述
     */
    @Override
    public String description() {
        return description;
    }


    /**
     * 函数参数
     */
    @Override
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    /**
     * 执行处理
     */
    @Override
    public String handle(Map<String, Object> args) throws Throwable {
        return handler.apply(args);
    }
}