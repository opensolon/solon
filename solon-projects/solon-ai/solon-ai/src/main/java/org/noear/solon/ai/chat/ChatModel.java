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

import org.noear.solon.ai.AiLLM;

import java.util.function.Consumer;

/**
 * 聊天模型
 *
 * @author noear
 * @since 3.1
 */
public interface ChatModel extends AiLLM<ChatMessage, ChatRequest> {
    /**
     * 提示语
     */
    default ChatRequest prompt(String content) {
        return prompt(ChatMessage.ofUser(content));
    }

    /**
     * 开始构建
     */
    static Builder of(String apiUrl) {
        return new ChatModelBuilderImpl(apiUrl);
    }

    /**
     * 聊天模型构建器
     */
    interface Builder {
        /**
         * 接口密钥
         */
        Builder apiKey(String apiKey);

        /**
         * 提供者
         */
        Builder provider(String provider);

        /**
         * 模型
         */
        Builder model(String model);

        /**
         * 头信息添加
         */
        Builder headerAdd(String key, String value);

        /**
         * 全局函数添加（方便组件模式）
         */
        Builder globalFunctionAdd(ChatFunction function);

        /**
         * 全局函数添加
         */
        Builder globalFunctionAdd(String name, Consumer<ChatFunctionDecl> functionBuilder);

        /**
         * 构建
         */
        ChatModel build();
    }
}