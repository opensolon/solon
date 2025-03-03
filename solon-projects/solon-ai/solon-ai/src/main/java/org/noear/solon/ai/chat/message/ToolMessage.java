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
import org.noear.solon.ai.chat.ChatRole;
import org.noear.solon.lang.Preview;

import java.util.Map;

/**
 * 聊天工具消息
 * 
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public class ToolMessage extends ChatMessageBase<ToolMessage> {
    private final ChatRole role = ChatRole.TOOL;
    private String content;
    private String name;
    private String toolCallId;

    public ToolMessage() {
        //用于序列化
    }

    public ToolMessage(String content, String name, String toolCallId) {
        this.content = content;
        this.name = name;
        this.toolCallId = toolCallId;
    }

    /**
     * 角色
     */
    @Override
    public ChatRole getRole() {
        return role;
    }

    @Override
    public String getContent() {
        return content;
    }

    /**
     * 函数名
     */
    public String getName() {
        return name;
    }

    /**
     * 工具调用标识
     */
    public String getToolCallId() {
        return toolCallId;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        buf.append(", role=").append(getRole().name().toLowerCase());

        if (content != null) {
            buf.append(", content='").append(content).append('\'');
        }

        if (Utils.isNotEmpty(metadata)) {
            buf.append(", metadata=").append(metadata);
        }

        if (name != null) {
            buf.append(", name='").append(name).append('\'');
        }

        if (toolCallId != null) {
            buf.append(", tool_call_id=").append(toolCallId);
        }

        buf.append("}");

        return buf.toString();
    }
}
