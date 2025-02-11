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

import java.time.Duration;
import java.util.function.Consumer;

/**
 * 聊天模型构建器实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatModelBuilder {
    private ChatConfig config = new ChatConfig();

    public ChatModelBuilder(String apiUrl) {
        config.apiUrl = apiUrl;
    }

    public ChatModelBuilder apiKey(String apiKey) {
        config.apiKey = apiKey;
        return this;
    }

    public ChatModelBuilder provider(String provider) {
        config.provider = provider;
        return this;
    }

    public ChatModelBuilder model(String model) {
        config.model = model;
        return this;
    }

    public ChatModelBuilder headerSet(String key, String value) {
        config.headers.put(key, value);
        return this;
    }

    public ChatModelBuilder globalFunctionAdd(ChatFunction function) {
        config.globalFunctions.put(function.name(), function);
        return this;
    }

    public ChatModelBuilder globalFunctionAdd(String name, Consumer<ChatFunctionDecl> functionBuilder) {
        ChatFunctionDecl decl = new ChatFunctionDecl(name);
        functionBuilder.accept(decl);
        globalFunctionAdd(decl);
        return this;
    }

    public ChatModelBuilder timeout(Duration timeout) {
        if (timeout != null) {
            config.timeout = timeout;
        }

        return this;
    }

    public ChatModel build() {
        return new ChatModelDefault(config);
    }
}