package org.noear.solon.ai.chat.message;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatRole;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2025/2/9 created
 */
public class SystemChatMessage implements ChatMessage {
    private String content;

    public SystemChatMessage(String content) {
        this.content = content;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.SYSTEM;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public ONode toRequestNode() {
        ONode oNode = new ONode();
        oNode.set("role", getRole().name().toLowerCase());
        oNode.set("content", content);
        return oNode;
    }

    @Override
    public String toString() {
        return "{" +
                "role='" + getRole() + '\'' +
                ",content='" + content + '\'' +
                '}';
    }
}