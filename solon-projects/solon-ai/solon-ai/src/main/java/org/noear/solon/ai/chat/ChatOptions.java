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

import org.noear.solon.ai.AiOptions;

import java.util.*;
import java.util.function.Consumer;

/**
 * 聊天选项
 *
 * @author noear
 * @since 3.1
 */
public class ChatOptions implements AiOptions {

    public static ChatOptions of() {
        return new ChatOptions();
    }

    private Long max_tokens;
    private Float temperature;
    private Float top_p;
    private Float top_k;
    private Float frequency_penalty;
    private final Map<String, ChatFunction> functions = new LinkedHashMap<>();

    public Float temperature() {
        return temperature;
    }

    public Collection<ChatFunction> functions() {
        return functions.values();
    }

    public ChatFunction function(String name) {
        return functions.get(name);
    }

    public ChatOptions temperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    public ChatOptions functionAdd(ChatFunction function) {
        functions.put(function.name(), function);
        return this;
    }

    public ChatOptions functionAdd(String name, Consumer<ChatFunctionDecl> functionBuilder) {
        ChatFunctionDecl decl = new ChatFunctionDecl(name);
        functionBuilder.accept(decl);
        functionAdd(decl);
        return this;
    }
}