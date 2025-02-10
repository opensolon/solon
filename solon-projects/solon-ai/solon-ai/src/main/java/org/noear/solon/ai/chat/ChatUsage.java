package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiUsage;

/**
 * @author noear 2025/2/10 created
 */
public interface ChatUsage extends AiUsage {
    long promptTokens();

    long completionTokens();

    long totalTokens();
}
