package org.noear.solon.ai.chat.message;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatRole;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2025/2/9 created
 */
public class AssistantChatMessage implements ChatMessage {
    private String content;
    private String reasoningContent;
    private ONode toolCalls;

    public AssistantChatMessage(String content, String reasoningContent, ONode toolCalls) {
        this.content = content;
        this.reasoningContent = reasoningContent;
        this.toolCalls = toolCalls;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.ASSISTANT;
    }

    @Override
    public String getContent() {
        return content;
    }

    public String getReasoningContent() {
        return reasoningContent;
    }

    public ONode getToolCalls() {
        return toolCalls;
    }

    @Override
    public ONode toRequestNode() {
        ONode oNode = new ONode();
        oNode.set("role", getRole().name().toLowerCase());
        oNode.set("content", content);

        if (Utils.isNotEmpty(reasoningContent)) {
            oNode.set("reasoning_content", reasoningContent);
        }

        if(toolCalls != null) {
            oNode.set("tool_calls", toolCalls);
        }

        return oNode;
    }

    @Override
    public String toString() {
        return "{" +
                "role='" + getRole() + '\'' +
                ",content='" + content + '\'' +
                ",reasoningContent='" + reasoningContent + '\'' +
                '}';
    }
}