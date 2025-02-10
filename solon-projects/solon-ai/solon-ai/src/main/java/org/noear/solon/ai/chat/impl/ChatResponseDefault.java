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

import org.noear.solon.ai.AiException;
import org.noear.solon.ai.chat.ChatChoice;
import org.noear.solon.ai.chat.ChatResponse;
import org.noear.solon.ai.chat.ChatUsage;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天响应
 *
 * @author noear
 * @since 3.1
 */
public class ChatResponseDefault implements ChatResponse {
    public AiException exception;
    public List<ChatChoice> choices = new ArrayList<>();
    public ChatUsage usage;
    public String model;
    public boolean finished;

    public void reset(){
        this.exception = null;
        this.choices.clear();
    }

    public String getModel() {
        return model;
    }

    @Override
    public AiException getException() {
        return exception;
    }

    @Override
    public List<ChatChoice> getChoices() {
        return choices;
    }

    public ChatChoice getChoice(int index){
        return choices.get(index);
    }

    @Override
    public ChatUsage getUsage() {
        return usage;
    }

    public boolean isFinished() {
        return finished;
    }
}