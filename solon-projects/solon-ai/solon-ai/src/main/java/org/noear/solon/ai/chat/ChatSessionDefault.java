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

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.lang.Preview;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天会话默认实现
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class ChatSessionDefault implements ChatSession {
    private final String sessionId;
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatSessionDefault() {
        this.sessionId = Utils.uuid();
    }

    public ChatSessionDefault(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 获取会话id
     */
    @Override
    public String getSessionId() {
        return sessionId;
    }

    /**
     * 获取所有消息
     */
    @Override
    public List<ChatMessage> getMessages() {
        return messages;
    }

    /**
     * 添加消息
     */
    @Override
    public void addMessage(ChatMessage... messages) {
        if (messages.length == 1) {
            this.messages.add(messages[0]);
        } else {
            for (ChatMessage m : messages) {
                this.messages.add(m);
            }
        }
    }

    /**
     * 清空消息
     */
    @Override
    public void clear() {
        this.messages.clear();
    }
}
