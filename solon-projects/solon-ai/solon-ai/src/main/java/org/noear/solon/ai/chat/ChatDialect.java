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
import org.noear.solon.ai.AiDialect;

import java.util.List;

/**
 * 聊天方言
 *
 * @author noear
 * @since 3.1
 */
public interface ChatDialect extends AiDialect {

    String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream);

    List<ChatFunctionCall> parseToolCalls(ChatConfig config,ONode toolCallsNode);

    boolean resolveResponseJson(ChatConfig config, ChatResponseDefault resp, String json);
}
