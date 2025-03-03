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

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.function.ChatFunctionCall;
import org.noear.solon.ai.chat.ChatRole;
import org.noear.solon.lang.Preview;

import java.util.*;

/**
 * 聊天助理消息
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class AssistantMessage extends ChatMessageBase<AssistantMessage> {
    private final ChatRole role = ChatRole.ASSISTANT;
    private String content;
    private List<ChatFunctionCall> toolCalls;
    private List<Map> toolCallsRaw;

    //纯思考；纯内容；混合内容
    private boolean isThinking;

    public AssistantMessage() {
        //用于序列化
    }

    public AssistantMessage(String content, boolean isThinking, List<Map> toolCallsRaw, List<ChatFunctionCall> toolCalls) {
        this.content = content;
        this.isThinking = isThinking;
        this.toolCallsRaw = toolCallsRaw;
        this.toolCalls = toolCalls;
    }

    /**
     * 角色
     */
    @Override
    public ChatRole getRole() {
        return role;
    }

    /**
     * 内容
     */
    @Override
    public String getContent() {
        return content;
    }

    /**
     * 结果内容（没有推理标签的内容）
     */
    private transient String resultContent;
    public String getResultContent() {
        if (resultContent == null) {
            if (content == null) {
                resultContent = "";
            } else {
                int tinkIndex = content.indexOf("</think>");
                if (tinkIndex > 0) {
                    resultContent = content.substring(tinkIndex + 8).trim();
                } else {
                    resultContent = content;
                }
            }
        }

        return resultContent;
    }

    /**
     * 是否思考中
     */
    @Override
    public boolean isThinking() {
        return isThinking;
    }

    /**
     * 工具调用
     */
    public List<ChatFunctionCall> getToolCalls() {
        return toolCalls;
    }

    /**
     * 工具高用原始数据（需要回传）
     */
    public List<Map> getToolCallsRaw() {
        return toolCallsRaw;
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

        if (Utils.isNotEmpty(metadata)) {
            buf.append(", metadata=").append(metadata);
        }

        if (toolCallsRaw != null) {
            buf.append(", tool_calls=").append(toolCallsRaw);
        }

        buf.append("}");

        return buf.toString();
    }
}