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

/**
 * 聊天消息实现
 *
 * @author noear
 * @since 3.1
 */
class ChatMessageImpl implements ChatMessage {
    private ChatRole role;
    private String name;
    private String content;
    private transient ONode tool_calls;

    public ChatMessageImpl(ChatRole role, String content, ONode tool_calls) {
        this.role = role;
        this.content = content;
        this.tool_calls = tool_calls;
    }

    public ChatMessageImpl(ChatRole role, String content) {
        this(role, content, null);
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public ChatRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getContent() {
        return content;
    }

    public ONode getToolCalls() {
        return tool_calls;
    }
}