package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiRequest;
import org.reactivestreams.Publisher;

import java.util.function.Consumer;

/**
 * @author noear 2025/1/28 created
 */
public class ChatRequest implements AiRequest<ChatOptions,ChatRequest, ChatResponse> {
    private final ChatConfig config;
    private final ChatMessage[] messages;
    private ChatOptions options;

    public ChatRequest(ChatConfig config, ChatMessage[] messages) {
        this.config = config;
        this.messages = messages;
        this.options = ChatOptions.ofDefault();
    }

    @Override
    public ChatRequest options(ChatOptions options) {
        if (options != null) {
            this.options = options;
        }

        return this;
    }

    @Override
    public ChatResponse call() {
        return null;
    }

    @Override
    public Publisher<ChatResponse> stream() {
        return null;
    }

}
