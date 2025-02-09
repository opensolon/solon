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
import org.reactivestreams.Subscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger log = LoggerFactory.getLogger(ChatRequestDefault.class);
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
        HttpUtils httpUtils = buildReqHttp();

        String reqJson = buildReqJson(false);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}",reqJson);
        }

        String respJson = httpUtils.bodyOfJson(reqJson).post();

        if (log.isTraceEnabled()) {
            log.trace("ai-response: {}",respJson);
        }

        ChatResponseDefault resp = new ChatResponseDefault();
        resp.resolve(respJson);

        if (resp.getException() != null) {
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
        HttpUtils httpUtils = buildReqHttp();

        String reqJson = buildReqJson(true);

        if (log.isTraceEnabled()) {
            log.trace("ai-request: {}",reqJson);
        }

        return subscriber -> {
            httpUtils.bodyOfJson(reqJson).execAsync("POST")
                    .whenComplete((resp, err) -> {
                        if (err == null) {
                            try {
                                parseResp(resp, subscriber);
                            } catch (IOException e) {
                                subscriber.onError(e);
                            }
                        } else {
                            subscriber.onError(err);
                        }
                    });
        };
    }

    private void parseResp(HttpResponse resp, Subscriber<? super ChatResponse> subscriber) throws IOException {
        ChatResponseDefault response = new ChatResponseDefault();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resp.body()))) {
            subscriber.onSubscribe(new SimpleSubscription().onRequest((subscription, l) -> {
                try {
                    while (l > 0) {
                        if (subscription.isCancelled()) {
                            break;
                        }

                        String respJson = reader.readLine();

                        if(respJson == null) {
                            break;
                        }

                        if (log.isTraceEnabled()) {
                            log.trace("ai-response: {}",respJson);
                        }

                        if(response.resolve(respJson)){
                            if (response.getException() != null) {
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
                    }
                } catch (Throwable err) {
                    subscriber.onError(err);
                }
            }));
        }
    }

    private void parseToolCalls(ONode toolCalls) {
        for (ONode n1 : toolCalls.ary()) {
            String callId = n1.get("id").getString();

            ONode n1f = n1.get("function");
            String name = n1f.get("name").getString();
            ONode n1fArgs = n1f.get("arguments");
            if(n1fArgs.isValue()){
                //有可能是 json string
                n1fArgs = ONode.loadStr(n1fArgs.getString());
            }

            Map<String, Object> args = new HashMap<>();
            n1fArgs.obj().forEach((k, v) -> {
                args.put(k, v.getString());
            });

            for (ChatFunction func : config.globalFunctions()) {
                if (name.equals(func.name())) {
                    String content = func.handle(args);
                    messages.add(ChatMessage.ofTool(content, name, callId));
                }
            }
        }
    }

    private HttpUtils buildReqHttp() {
        HttpUtils httpUtils = HttpUtils.http(config.apiUrl());

        if (Utils.isNotEmpty(config.apiKey())) {
            httpUtils.header("Authorization", "Bearer " + config.apiKey());
        }

        httpUtils.headers(config.headers());

        return httpUtils;
    }

    private String buildReqJson(boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (options.temperature() != null) {
                n.set("temperature", options.temperature());
            }

            if (Utils.isNotEmpty(config.model())) {
                n.set("model", config.model());
            }

            n.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    n1.add(m1.toRequestNode());
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
                                        if (p1.type().isArray()) {
                                            n6.set("type", "array");
                                            n6.getOrNew("items").set("type", p1.typeAsString()); //todo:...要改
                                        } else {
                                            n6.set("type", p1.typeAsString());
                                        }

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