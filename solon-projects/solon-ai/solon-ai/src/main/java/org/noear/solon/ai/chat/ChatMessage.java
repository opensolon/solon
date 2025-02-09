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

import org.noear.snack.ONode;
import org.noear.solon.ai.AiMessage;
import org.noear.solon.ai.chat.message.AssistantChatMessage;
import org.noear.solon.ai.chat.message.SystemChatMessage;
import org.noear.solon.ai.chat.message.ToolChatMessage;
import org.noear.solon.ai.chat.message.UserChatMessage;

import java.util.Map;

/**
 * 聊天消息
 *
 * @author noear
 * @since 3.1
 */
public interface ChatMessage extends AiMessage {
    /**
     * 获取角色
     */
    ChatRole getRole();

    /**
     * 转为请求字典
     */
    ONode toRequestNode();

    static AssistantChatMessage of(ONode oMessage) {
        //String role = oMessage.get("role").getString();
        String content = oMessage.get("content").getString();
        String reasoning_content = oMessage.get("reasoning_content").getString();
        ONode toolCalls = oMessage.getOrNull("tool_calls");

        return new AssistantChatMessage(content, reasoning_content, toolCalls);
    }

    /**
     * 构建系统消息
     */
    static ChatMessage ofSystem(String content) {
        return new SystemChatMessage(content);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content) {
        return new UserChatMessage(content);
    }

    /**
     * 构建助理消息
     */
    static ChatMessage ofAssistant(String content) {
        return new AssistantChatMessage(content, null, null);
    }

    /**
     * 构建工具消息
     */
    static ChatMessage ofTool(String content, String name, String id) {
        return new ToolChatMessage(content, name, id);
    }
}