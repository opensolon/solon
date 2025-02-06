package org.noear.solon.ai.chat;

import org.noear.solon.ai.chat.impl.ChatModelDefault;

/**
 * @author noear 2025/2/6 created
 */
public class ChatModelBuilderImpl implements ChatModel.Builder{
    private ChatConfig config = new ChatConfig();

    public  ChatModelBuilderImpl(String apiUrl) {
        config.apiUrl = apiUrl;
    }

    public ChatModel.Builder apiKey(String apiKey) {
        config.apiKey = apiKey;
        return this;
    }

    public ChatModel.Builder provider(String provider) {
        config.provider = provider;
        return this;
    }

    public ChatModel.Builder model(String model) {
        config.model = model;
        return this;
    }

    public ChatModel.Builder headerAdd(String key, String value) {
        config.headers.add(key, value);
        return this;
    }

    public ChatModel build() {
        return new ChatModelDefault(config);
    }
}