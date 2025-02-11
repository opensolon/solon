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

import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionDecl;
import org.noear.solon.lang.Preview;

import java.util.*;
import java.util.function.Consumer;

/**
 * 聊天选项
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class ChatOptions {

    public static ChatOptions of() {
        return new ChatOptions();
    }

    private Long max_tokens;
    private Long max_completion_tokens;
    private Float temperature;
    private Float top_p;
    private Float top_k;
    private Float frequency_penalty;
    private Boolean store;

    private final Map<String, ChatFunction> functions = new LinkedHashMap<>();

    public Long max_tokens() {
        return max_tokens;
    }

    public ChatOptions max_tokens(long max_tokens) {
        this.max_tokens = max_tokens;
        return this;
    }

    public Long max_completion_tokens() {
        return max_completion_tokens;
    }

    public ChatOptions max_completion_tokens(long max_completion_tokens) {
        this.max_completion_tokens = max_completion_tokens;
        return this;
    }

    public Float temperature() {
        return temperature;
    }

    public ChatOptions temperature(float temperature) {
        this.temperature = temperature;
        return this;
    }

    public Float top_p() {
        return top_p;
    }

    public ChatOptions top_p(float top_p) {
        this.top_p = top_p;
        return this;
    }

    public Float top_k() {
        return top_k;
    }

    public ChatOptions top_k(float top_k) {
        this.top_k = top_k;
        return this;
    }

    public Float frequency_penalty() {
        return frequency_penalty;
    }

    public ChatOptions frequency_penalty(float frequency_penalty) {
        this.frequency_penalty = frequency_penalty;
        return this;
    }

    public Boolean store() {
        return store;
    }

    public ChatOptions store(boolean store) {
        this.store = store;
        return this;
    }

    /// ////////////

    public Collection<ChatFunction> functions() {
        return functions.values();
    }

    public ChatFunction function(String name) {
        return functions.get(name);
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