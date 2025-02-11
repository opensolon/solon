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

import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatRole;

/**
 * 聊天工具消息
 * 
 * @author noear
 * @since 3.1
 */
public class ToolChatMessage implements ChatMessage {
    private String content;
    private String name;
    private String id;

    public ToolChatMessage(String content, String name, String id) {
        this.content = content;
        this.name = name;
        this.id = id;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.TOOL;
    }

    @Override
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{");

        buf.append(", role=").append(getRole().name().toLowerCase());

        if (content != null) {
            buf.append(", content='").append(content).append('\'');
        }

        if (name != null) {
            buf.append(", name='").append(name).append('\'');
        }

        if (id != null) {
            buf.append(", tool_call_id=").append(id);
        }

        buf.append("}");

        return buf.toString();
    }
}
