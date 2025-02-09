package org.noear.solon.ai.chat.message;

import org.noear.snack.ONode;
import org.noear.snack.core.NodeEncoder;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.ChatMessage;
import org.noear.solon.ai.chat.ChatRole;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author noear 2025/2/9 created
 */
public class ToolChatMessage implements ChatMessage {
    private String content;
    private String name;
    private String id;

    public ToolChatMessage(String content, String name, String id) {
        this.content = content;
        this.name = name;
        this.id = id;
    }

    @Override
    public ChatRole getRole() {
        return ChatRole.TOOL;
    }

    @Override
    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    @Override
    public ONode toRequestNode() {
        ONode oNode = new ONode();

        oNode.set("role", getRole().name().toLowerCase());
        oNode.set("content", content);
        oNode.set("name", name);

        if (Utils.isNotEmpty(id)) {
            oNode.set("tool_call_id", id);
        }

        return oNode;
    }

    @Override
    public String toString() {
        return "{" +
                "role='" + getRole() + '\'' +
                ",content='" + content + '\'' +
                ",name='" + name + '\'' +
                ",id='" + id + '\'' +
                '}';
    }
}
