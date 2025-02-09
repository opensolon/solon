/*
 * Copyright 2017-2025 noear.org and authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.noear.solon.ai.chat.impl;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.*;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.rx.SimpleSubscription;
import org.reactivestreams.Publisher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author noear
 * @since 3.1
 */
public class ChatRequestDefault implements ChatRequest {
    private static final ChatOptions OPTIONS_DEFAULT = new ChatOptions();

    private final ChatConfig config;
    private final List<ChatMessage> messages;
    private ChatOptions options;

    public ChatRequestDefault(ChatConfig config, List<ChatMessage> messages) {
        this.config = config;
        this.messages = new ArrayList<>(messages);
        this.options = OPTIONS_DEFAULT;
    }

    @Override
    public ChatRequest options(ChatOptions options) {
        if (options != null) {
            this.options = options;
        }

        return this;
    }

    @Override
    public ChatRequest options(Consumer<ChatOptions> optionsBuilder) {
        this.options = ChatOptions.of();
        optionsBuilder.accept(options);
        return this;
    }

    @Override
    public ChatResponse call() throws IOException {
        HttpUtils httpUtils = HttpUtils.http(config.apiUrl());

        if (Utils.isNotEmpty(config.apiKey())) {
            httpUtils.header("Authorization", "Bearer " + config.apiKey());
        }

        String reqJson = buildReqJson(false);

        String respJson = httpUtils.bodyOfJson(reqJson).post();

        ChatResponseDefault resp = new ChatResponseDefault().resolve(respJson);

        if(resp.getException() != null) {
            throw resp.getException();
        }

        if (resp.getMessage().getToolCalls() != null) {
            messages.add(resp.getMessage());
            parseToolCalls(resp.getMessage().getToolCalls());

            return call();
        } else {
            return resp;
        }
    }

    @Override
    public Publisher<ChatResponse> stream() {
        HttpUtils httpUtils = HttpUtils.http(config.apiUrl());

        if (Utils.isNotEmpty(config.apiKey())) {
            httpUtils.header("Authorization", "Bearer " + config.apiKey());
        }

        String reqJson = buildReqJson(true);

        return subscriber -> {
            ChatResponseDefault response = new ChatResponseDefault();
            try (HttpResponse resp = httpUtils.bodyOfJson(reqJson).exec("POST")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(resp.body()));
                subscriber.onSubscribe(new SimpleSubscription()
                        .onRequest((my, l) -> {
                            try {
                                while (l > 0) {
                                    if (my.isCancelled()) {
                                        break;
                                    }

                                    response.resolve(reader.readLine());

                                    if(response.getException() != null) {
                                        //空读一行（流读取时，一行a）
                                        reader.readLine();

                                        subscriber.onError(response.getException());
                                        return;
                                    }

                                    if (response.getMessage().getToolCalls() != null) {
                                        messages.add(response.getMessage());
                                        parseToolCalls(response.getMessage().getToolCalls());

                                        //空读一行（流读取时，一行a）
                                        reader.readLine();

                                        stream().subscribe(subscriber);
                                        return;
                                    }

                                    subscriber.onNext(response);

                                    if (response.isDone()) {
                                        subscriber.onComplete();
                                        break;
                                    } else {
                                        l--;
                                    }
                                }
                            } catch (Throwable err) {
                                subscriber.onError(err);
                            }
                        }));

            } catch (Throwable err) {
                subscriber.onError(err);
            }
        };
    }

    private void parseToolCalls(ONode toolCalls) {
        for (ONode n1 : toolCalls.ary()) {
            ONode n1f = n1.get("function");
            String name = n1f.get("name").getString();
            Map<String, Object> args = new HashMap<>();
            n1f.get("arguments").obj().forEach((k, v) -> {
                args.put(k, v.getString());
            });

            for (ChatFunction func : config.globalFunctions()) {
                if (name.equals(func.name())) {
                    String content = func.handle(args);
                    messages.add(ChatMessage.ofTool(name, content));
                }
            }
        }
    }

    private String buildReqJson(boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (options.temperature() != ChatOptions.TEMPERATURE_DEFAULT) {
                n.set("temperature", options.temperature());
            }

            if (Utils.isNotEmpty(config.model())) {
                n.set("model", config.model());
            }

            n.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    n1.addNew().build(n2 -> {
                        n2.set("role", m1.getRole().name().toLowerCase());
                        n2.set("content", m1.getContent());
                        if (Utils.isNotEmpty(m1.getName())) {
                            n2.set("name", m1.getName());
                        }
                    });
                }
            });

            buildReqFunctionsJson(n, config.globalFunctions());
            buildReqFunctionsJson(n, options.functions());
        }).toJson();
    }

    private void buildReqFunctionsJson(ONode n, List<ChatFunction> funcs) {
        if (Utils.isEmpty(funcs)) {
            return;
        }

        n.getOrNew("tools").build(n1 -> {
            for (ChatFunction func : funcs) {
                n1.addNew().build(n2 -> {
                    n2.set("type", "function");
                    n2.getOrNew("function").build(n3 -> {
                        n3.set("name", func.name());
                        n3.set("description", func.description());
                        n3.getOrNew("parameters").build(n4 -> {
                            n4.set("type", "object");
                            ONode n4r = n4.getOrNew("required").asArray();
                            n4.getOrNew("properties").build(n5 -> {
                                for (ChatFunctionParam p1 : func.params()) {
                                    n5.getOrNew(p1.name()).build(n6 -> {
                                        n6.set("type", p1.type());
                                        n6.set("description", p1.description());
                                    });
                                    n4r.add(p1.name());
                                }
                            });
                        });
                    });
                });
            }
        });
    }
}