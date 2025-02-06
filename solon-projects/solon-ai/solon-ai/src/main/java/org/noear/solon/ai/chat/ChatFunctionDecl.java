package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * @author noear 2025/2/6 created
 */
public class ChatFunctionDecl implements AiFunction<ChatFunctionParamDecl> {
    private final String name;
    private final List<ChatFunctionParamDecl> params;
    private String description;

    public ChatFunctionDecl(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public ChatFunctionDecl description(String description) {
        this.description = description;
        return this;
    }

    public ChatFunctionDecl paramAdd(String name, String type, String description) {
        params.add(new ChatFunctionParamDecl(name, type, description));
        return this;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String description() {
        return description;
    }

    @Override
    public Iterable<ChatFunctionParamDecl> params() {
        return params();
    }
}
