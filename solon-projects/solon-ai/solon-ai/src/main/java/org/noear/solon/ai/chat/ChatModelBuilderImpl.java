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

import org.noear.solon.ai.chat.impl.ChatModelDefault;

import java.util.function.Consumer;

/**
 * 聊天模型构建器实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatModelBuilderImpl implements ChatModel.Builder {
    private ChatConfig config = new ChatConfig();

    public ChatModelBuilderImpl(String apiUrl) {
        config.apiUrl = apiUrl;
    }

    public ChatModel.Builder apiKey(String apiKey) {
        config.apiKey = apiKey;
        return this;
    }

    public ChatModel.Builder provider(String provider) {
        config.provider = provider;
        return this;
    }

    public ChatModel.Builder model(String model) {
        config.model = model;
        return this;
    }

    public ChatModel.Builder headerAdd(String key, String value) {
        config.headers.add(key, value);
        return this;
    }

    @Override
    public ChatModel.Builder globalFunctionAdd(ChatFunction function) {
        config.globalFunctions.put(function.name(), function);
        return this;
    }

    @Override
    public ChatModel.Builder globalFunctionAdd(String name, Consumer<ChatFunctionDecl> functionBuilder) {
        ChatFunctionDecl decl = new ChatFunctionDecl(name);
        functionBuilder.accept(decl);
        globalFunctionAdd(decl);
        return this;
    }

    public ChatModel build() {
        config.dialect = ChatDialectManager.get(config.provider);

        return new ChatModelDefault(config);
    }
}