package org.noear.solon.ai.chat;

import org.noear.solon.ai.LLM;

import java.util.function.Consumer;

/**
 * @author noear 2025/1/27 created
 */
public interface ChatModel extends LLM<ChatMessage> {
    ChatModel consumer(Consumer<ChatMessage> consumer);
}
