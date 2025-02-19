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

import org.noear.solon.ai.chat.annotation.FunctionMapping;
import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionDecl;
import org.noear.solon.ai.chat.functioncall.MethodChatFunction;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * 聊天模型构建器实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatModelBuilder {
    private final ChatConfig config;

    public ChatModelBuilder(String apiUrl) {
        this.config = new ChatConfig();
        this.config.setApiUrl(apiUrl);
    }

    public ChatModelBuilder(ChatConfig config) {
        this.config = config;
    }

    public ChatModelBuilder apiKey(String apiKey) {
        config.setApiKey(apiKey);
        return this;
    }

    public ChatModelBuilder provider(String provider) {
        config.setProvider(provider);
        return this;
    }

    public ChatModelBuilder model(String model) {
        config.setModel(model);
        return this;
    }

    public ChatModelBuilder headerSet(String key, String value) {
        config.setHeader(key, value);
        return this;
    }

    /**
     * 函数添加
     *
     * @param functionObj 函数对象
     */
    public ChatModelBuilder globalFunctionAdd(Object functionObj) {
        return globalFunctionAdd(functionObj.getClass(), functionObj);
    }

    /**
     * 函数添加
     *
     * @param functionClz 函数类（如果函数对象为代理时，必须传入原始类）
     * @param functionObj 函数对象
     */
    public ChatModelBuilder globalFunctionAdd(Class<?> functionClz, Object functionObj) {
        if (functionObj instanceof ChatFunction) {
            ChatFunction func = (ChatFunction) functionObj;
            config.globalFunctions.put(func.name(), func);
        } else {
            int count = 0;
            for (Method method : functionClz.getMethods()) {
                if (method.isAnnotationPresent(FunctionMapping.class)) {
                    MethodChatFunction func = new MethodChatFunction(functionObj, method);
                    config.globalFunctions.put(func.name(), func);
                    count++;
                }
            }

            if (count == 0) {
                throw new IllegalArgumentException("This functionBean is not ChatFunction");
            }
        }

        return this;
    }

    public ChatModelBuilder globalFunctionAdd(String name, Consumer<ChatFunctionDecl> functionBuilder) {
        ChatFunctionDecl decl = new ChatFunctionDecl(name);
        functionBuilder.accept(decl);
        globalFunctionAdd(decl);
        return this;
    }

    public ChatModelBuilder timeout(Duration timeout) {
        config.setTimeout(timeout);

        return this;
    }

    public ChatModel build() {
        return new ChatModel(config);
    }
}