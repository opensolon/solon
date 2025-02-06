package org.noear.solon.ai.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author noear 2025/2/6 created
 */
public class ChatFunctionDecl implements ChatFunction {
    private final String name;
    private final List<ChatFunctionParam> params;
    private String description;
    private Function<Map<String, Object>, String> handler;

    public ChatFunctionDecl(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public ChatFunctionDecl description(String description) {
        this.description = description;
        return this;
    }

    public ChatFunctionDecl param(String name, String type, String description) {
        params.add(new ChatFunctionParamDecl(name, type, description));
        return this;
    }

    public ChatFunctionDecl stringParam(String name, String description) {
        return param(name, "string", description);
    }

    public ChatFunctionDecl intParam(String name, String description) {
        return param(name, "int", description);
    }

    public ChatFunctionDecl floatParam(String name, String description) {
        return param(name, "float", description);
    }

    public ChatFunctionDecl handle(Function<Map<String, Object>, String> handler) {
        this.handler = handler;
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
    public Iterable<ChatFunctionParam> params() {
        return params;
    }

    @Override
    public String handle(Map<String, Object> args) {
        return handler.apply(args);
    }
}
