package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiToolCall;

import java.util.Map;

/**
 * @author noear 2025/2/9 created
 */
public class ChatFunctionCall implements AiToolCall {
    private String id;
    private String name;
    private Map<String, Object> arguments;

    public ChatFunctionCall(String id, String name, Map<String, Object> arguments) {
        this.id = id;
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String type() {
        return "function";
    }

    public String name() {
        return name;
    }

    public Map<String, Object> arguments() {
        return arguments;
    }
}
