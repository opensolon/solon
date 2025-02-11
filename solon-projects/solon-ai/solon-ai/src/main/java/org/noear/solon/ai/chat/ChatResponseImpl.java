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

import org.noear.solon.Utils;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.lang.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天响应实现
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseImpl implements ChatResponse {
    public final List<ChatChoice> choices = new ArrayList<>();
    public ChatException exception;
    public ChatUsage usage;
    public String model;
    public boolean finished;

    public void reset() {
        this.exception = null;
        this.choices.clear();
    }

    public String getModel() {
        return model;
    }

    @Override
    public ChatException getException() {
        return exception;
    }

    @Override
    public List<ChatChoice> getChoices() {
        return choices;
    }

    @Override
    public boolean hasMessage() {
        return Utils.isNotEmpty(choices);
    }

    @Override
    public AssistantMessage getMessage() {
        if (hasMessage()) {
            return choices.get(0).getMessage();
        } else {
            return null;
        }
    }

    //完成时，才会有使用情况
    @Override
    public @Nullable ChatUsage getUsage() {
        return usage;
    }

    @Override
    public boolean isFinished() {
        return finished;
    }
}