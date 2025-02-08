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
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatResponse;

/**
 * 聊天响应
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseDefault implements ChatResponse {
    private ChatMessage message;
    private String model;
    private String created_at;
    private String done_reason;
    private boolean done;
    private long total_duration;
    private long load_duration;
    private long prompt_eval_count;
    private long prompt_eval_duration;
    private long eval_count;
    private long eval_duration;

    public ChatResponseDefault load(String json) {
        //解析
        ONode oResp = ONode.load(json);

        this.model = oResp.get("model").getString();
        this.created_at = oResp.get("created_at").getString();
        this.done = oResp.get("done").getBoolean();
        this.message = ChatMessage.of(oResp.get("message"));

        if (done) {
            this.done_reason = oResp.get("done_reason").getString();
            this.total_duration = oResp.get("total_duration").getLong();
            this.load_duration = oResp.get("load_duration").getLong();
            this.prompt_eval_count = oResp.get("prompt_eval_count").getLong();
            this.prompt_eval_duration = oResp.get("prompt_eval_duration").getLong();
            this.eval_count = oResp.get("eval_count").getLong();
            this.eval_duration = oResp.get("eval_duration").getLong();
        }

        return this;
    }

    public String getModel() {
        return model;
    }

    public String getCreatedAt() {
        return created_at;
    }

    @Override
    public ChatMessage getMessage() {
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
