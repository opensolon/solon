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
public class AssistantMessage implements ChatMessage {
    private String content;
    private String reasoningContent;
    private List<ChatFunctionCall> toolCalls;
    private ONode toolCallsNode;

    public AssistantMessage(String content, String reasoningContent, ONode toolCallsNode, List<ChatFunctionCall> toolCalls) {
        this.content = content;
        this.reasoningContent = reasoningContent;
        this.toolCallsNode = toolCallsNode;
        this.toolCalls = toolCalls;
    }

    /**
     * 获取角色
     */
    @Override
    public ChatRole getRole() {
        return ChatRole.ASSISTANT;
    }

    /**
     * 获取内容
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * 获取思考内容
     */
    public String getReasoningContent() {
        return reasoningContent;
    }

    /**
     * 获取工具调用
     */
    public List<ChatFunctionCall> getToolCalls() {
        return toolCalls;
    }

    /**
     * 获取工具高用原始数据（需要回传）
     */
    public ONode getToolCallsRaw() {
        return toolCallsNode;
    }

    /**
     * 转为字符串
     */
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