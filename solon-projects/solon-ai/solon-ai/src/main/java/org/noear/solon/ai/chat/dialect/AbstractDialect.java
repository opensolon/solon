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
package org.noear.solon.ai.chat.dialect;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.*;

import java.util.*;

/**
 * @author noear
 * @since 3.1
 */
public abstract class AbstractDialect implements ChatDialect {

    @Override
    public String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (Utils.isNotEmpty(config.model())) {
                n.set("model", config.model());
            }

            n.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    n1.add(m1.toRequestNode());
                }
            });

            if(options.max_tokens() !=null){
                n.set("max_tokens", options.max_tokens());
            }

            if (options.temperature() != null) {
                n.set("temperature", options.temperature());
            }

            if(options.top_p() !=null){
                n.set("top_p", options.top_p());
            }

            if(options.top_k() !=null){
                n.set("top_k", options.top_k());
            }

            if(options.frequency_penalty() !=null){
                n.set("frequency_penalty", options.frequency_penalty());
            }

            ChatMessage lastMessage = messages.get(messages.size() - 1);
            buildReqFunctionsJson(n, config, options, lastMessage);
        }).toJson();
    }

    protected void buildReqFunctionsJson(ONode n, ChatConfig config, ChatOptions options, ChatMessage lastMessage) {
        buildReqFunctionsJsonDo(n, config.globalFunctions());
        buildReqFunctionsJsonDo(n, options.functions());
    }

    protected void buildReqFunctionsJsonDo(ONode n, Collection<ChatFunction> funcs) {
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

    @Override
    public List<ChatFunctionCall> parseToolCalls(ChatConfig config, ONode toolCallsNode) {
        if (toolCallsNode == null) {
            return null;
        }

        List<ChatFunctionCall> toolCalls = new ArrayList<>();

        for (ONode n1 : toolCallsNode.ary()) {
            String callId = n1.get("id").getString();

            ONode n1f = n1.get("function");
            String name = n1f.get("name").getString();
            ONode n1fArgs = n1f.get("arguments");
            if (n1fArgs.isValue()) {
                //有可能是 json string
                n1fArgs = ONode.loadStr(n1fArgs.getString());
            }

            Map<String, Object> args = new HashMap<>();
            n1fArgs.obj().forEach((k, v) -> {
                args.put(k, v.getString());
            });

            toolCalls.add(new ChatFunctionCall(callId, name, args));
        }

        return toolCalls;
    }
}