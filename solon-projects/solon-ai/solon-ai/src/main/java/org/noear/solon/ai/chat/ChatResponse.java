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

import org.noear.solon.ai.AiUsage;
import org.noear.solon.ai.chat.message.AssistantMessage;
import org.noear.solon.lang.Nullable;
import org.noear.solon.lang.Preview;

import java.util.List;

/**
 * 聊天响应
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface ChatResponse {
    /**
     * 获取模型
     */
    String getModel();

    /**
     * 获取错误
     */
    @Nullable
    ChatException getError();

    /**
     * 是否有选择
     */
    boolean hasChoices();

    /**
     * 获取所有选择
     */
    List<ChatChoice> getChoices();

    /**
     * 获取消息
     */
    @Nullable
    AssistantMessage getMessage();

    /**
     * 获取使用情况（完成时，才会有使用情况）
     */
    @Nullable
    AiUsage getUsage();

    /**
     * 是否完成
     */
    boolean isFinished();
}