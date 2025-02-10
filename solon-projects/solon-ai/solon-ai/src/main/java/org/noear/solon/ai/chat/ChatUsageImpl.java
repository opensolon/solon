package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiUsage;

/**
 * @author noear 2025/2/10 created
 */
public class ChatUsageImpl implements ChatUsage {
    public long promptTokens;
    public long completionTokens;
    public long totalTokens;

    @Override
    public long promptTokens() {
        return 0;
    }

    @Override
    public long completionTokens() {
        return 0;
    }

    @Override
    public long totalTokens() {
        return 0;
    }
}
