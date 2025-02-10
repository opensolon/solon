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

import java.util.List;

/**
 * 聊天模型实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatModelDefault implements ChatModel {
    private final ChatConfig config;

    public ChatModelDefault(ChatConfig config) {
        config.dialect = ChatDialectManager.get(config);

        this.config = config;
    }

    @Override
    public ChatRequest prompt(List<ChatMessage> messages) {
        return new ChatRequestDefault(config, messages);
    }
}
