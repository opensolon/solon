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
import java.util.List;
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
        this.messages = messages;
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

        return new ChatResponseDefault().load(respJson);
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

                                    response.load(reader.readLine());
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

    private String buildReqJson(boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);
            n.set("model", config.model());
            n.getOrNew("messages").build(m -> {
                for (ChatMessage msg : messages) {
                    m.addNew().set("role", msg.getRole().name().toLowerCase())
                            .set("content", msg.getContent());
                }
            });
        }).toJson();
    }
}