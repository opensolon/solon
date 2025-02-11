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

import org.noear.solon.ai.chat.message.AssistantMessage;

import java.util.Date;

/**
 * 聊天响应选择
 *
 * @author noear
 * @since 3.1
 */
public class ChatChoice {
    private final int index;
    private final Date created;
    private final String finishReason;
    private final AssistantMessage message;

    public ChatChoice(int index, Date created, String finishReason, AssistantMessage message) {
        this.index = index;
        this.created = created;
        this.finishReason = finishReason;
        this.message = message;
    }

    public int index() {
        return index;
    }

    public Date getCreated() {
        return created;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public AssistantMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", created=" + created +
                ", finishReason='" + finishReason + '\'' +
                ", message=" + message +
                '}';
    }
}
