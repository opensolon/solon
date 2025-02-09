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
package org.noear.solon.ai.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author noear
 * @since 3.1
 */
public class ChatFunctionDecl implements ChatFunction {
    private final String name;
    private final List<ChatFunctionParam> params;
    private String description;
    private Function<Map<String, Object>, String> handler;

    public ChatFunctionDecl(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public ChatFunctionDecl description(String description) {
        this.description = description;
        return this;
    }

    public ChatFunctionDecl param(String name, Class<?> type, String description) {
        if (type.isPrimitive() == false) {
            throw new IllegalArgumentException("type must be primitive");
        }

        params.add(new ChatFunctionParamDecl(name, type, description));
        return this;
    }

    public ChatFunctionDecl stringParam(String name, String description) {
        return param(name, String.class, description);
    }

    public ChatFunctionDecl intParam(String name, String description) {
        return param(name, int.class, description);
    }

    public ChatFunctionDecl floatParam(String name, String description) {
        return param(name, float.class, description);
    }

    public ChatFunctionDecl handle(Function<Map<String, Object>, String> handler) {
        this.handler = handler;
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    @Override
    public String handle(Map<String, Object> args) {
        return handler.apply(args);
    }
}