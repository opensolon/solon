package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiFunctionParam;

/**
 * @author noear 2025/2/6 created
 */
public class ChatFunctionParamDecl implements AiFunctionParam {
    private final String name;
    private final String type;
    private final String description;

    public ChatFunctionParamDecl(String name, String type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public String description() {
        return description;
    }
}
