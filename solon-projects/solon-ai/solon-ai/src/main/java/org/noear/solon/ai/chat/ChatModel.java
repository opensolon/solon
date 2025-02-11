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

import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.lang.Preview;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 聊天模型
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface ChatModel {
    /**
     * 提示语
     */
    ChatRequest prompt(List<ChatMessage> messages);

    /**
     * 提示语
     */
    default ChatRequest prompt(ChatMessage... messages) {
        return prompt(new ArrayList<>(Arrays.asList(messages)));
    }

    /**
     * 提示语
     */
    default ChatRequest prompt(String content) {
        return prompt(ChatMessage.ofUser(content));
    }


    /// /////////////////////////////////

    /**
     * 构建
     */
    static ChatModelBuilder of(ChatConfig config) {
        return new ChatModelBuilder(config);
    }

    /**
     * 开始构建
     */
    static ChatModelBuilder of(String apiUrl) {
        return new ChatModelBuilder(apiUrl);
    }
}