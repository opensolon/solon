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
package org.noear.solon.ai.chat.impl;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.ChatConfig;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.message.AssistantChatMessage;

/**
 * 聊天响应
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseDefault implements ChatResponse {
    public AiException exception;
    public AssistantChatMessage message;
    public String model;
    public String created_at;
    public String done_reason;
    public boolean done;
    public long total_duration;
    public long load_duration;
    public long prompt_eval_count;
    public long prompt_eval_duration;
    public long eval_count;
    public long eval_duration;

    public String getModel() {
        return model;
    }

    public String getCreatedAt() {
        return created_at;
    }

    @Override
    public AiException getException() {
        return exception;
    }

    @Override
    public AssistantChatMessage getMessage() {
        return message;
    }

    public String getDoneReason() {
        return done_reason;
    }

    public boolean isDone() {
        return done;
    }

    public long getTotalDuration() {
        return total_duration;
    }

    public long getLoadDuration() {
        return load_duration;
    }

    public long getPromptEvalCount() {
        return prompt_eval_count;
    }

    public long getPromptEvalDuration() {
        return prompt_eval_duration;
    }

    public long getEvalCount() {
        return eval_count;
    }

    public long getEvalDuration() {
        return eval_duration;
    }
}