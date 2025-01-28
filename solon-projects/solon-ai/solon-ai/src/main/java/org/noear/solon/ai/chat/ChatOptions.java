package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiOptions;

/**
 * @author noear 2025/1/28 created
 */
public class ChatOptions implements AiOptions {
    private static ChatOptions instance = new ChatOptions();

    protected static ChatOptions ofDefault() {
        return instance;
    }

    public static ChatOptions of() {
        return new ChatOptions();
    }

    private float temperature = 0.8f;

    public ChatOptions temperature(float temperature) {
        this.temperature = temperature;
        return this;
    }
}
