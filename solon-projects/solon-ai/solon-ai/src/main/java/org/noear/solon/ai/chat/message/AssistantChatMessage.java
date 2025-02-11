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
package org.noear.solon.ai.chat.message;

import org.noear.snack.ONode;
import org.noear.solon.ai.chat.functioncall.ChatFunctionCall;
import org.noear.solon.ai.chat.ChatRole;

import java.util.*;

/**
 * 聊天助理消息
 *
 * @author noear
 * @since 3.1
 */
public class AssistantChatMessage implements ChatMessage {
    private String content;
    private String reasoningContent;
    private List<ChatFunctionCall> toolCalls;
    private ONode toolCallsNode;

    public AssistantChatMessage(String content, String reasoningContent, ONode toolCallsNode, List<ChatFunctionCall> toolCalls) {
        this.content = content;
        this.reasoningContent = reasoningContent;
        this.toolCallsNode = toolCallsNode;
        this.toolCalls = toolCalls;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.ASSISTANT;
    }

    @Override
    public String getContent() {
        return content;
    }

    public String getReasoningContent() {
        return reasoningContent;
    }

    public List<ChatFunctionCall> getToolCalls() {
        return toolCalls;
    }

    public ONode getToolCallsNode() {
        return toolCallsNode;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        buf.append("role=").append(getRole().name().toLowerCase());

        if (content != null) {
            buf.append(", content='").append(content).append('\'');
        }

        if (reasoningContent != null) {
            buf.append(", reasoning_content='").append(reasoningContent).append('\'');
        }

        if (toolCallsNode != null) {
            buf.append(", tool_calls=").append(toolCallsNode);
        }

        buf.append("}");

        return buf.toString();
    }
}