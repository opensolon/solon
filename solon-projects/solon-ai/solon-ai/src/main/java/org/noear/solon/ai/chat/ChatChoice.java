package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiChoice;
import org.noear.solon.ai.chat.message.AssistantChatMessage;

import java.util.Date;

/**
 * @author noear 2025/2/10 created
 */
public class ChatChoice implements AiChoice<AssistantChatMessage> {
    private final int index;
    private final Date created;
    private final String finishReason;
    private final AssistantChatMessage message;

    public ChatChoice(int index, Date created, String finishReason, AssistantChatMessage message) {
        this.index = index;
        this.created = created;
        this.finishReason = finishReason;
        this.message = message;
    }

    @Override
    public int index() {
        return index;
    }

    public Date getCreated() {
        return created;
    }

    public String getFinishReason() {
        return finishReason;
    }

    @Override
    public AssistantChatMessage getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "{" +
                "index=" + index +
                ", created=" + created +
                ", finishReason='" + finishReason + '\'' +
                ", message=" + message +
                '}';
    }
}
