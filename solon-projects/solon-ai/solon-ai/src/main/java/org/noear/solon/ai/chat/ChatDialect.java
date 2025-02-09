package org.noear.solon.ai.chat;

import org.noear.snack.ONode;
import org.noear.solon.ai.AiDialect;
import org.noear.solon.ai.chat.impl.ChatResponseDefault;

import java.util.List;

/**
 * @author noear 2025/2/9 created
 */
public interface ChatDialect extends AiDialect {

    String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream);

    List<ChatFunctionCall> parseToolCalls(ChatConfig config,ONode toolCallsNode);

    boolean resolveResponseJson(ChatConfig config, ChatResponseDefault resp, String json);
}
