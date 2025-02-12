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
import org.noear.solon.ai.chat.functioncall.ChatFunction;
import org.noear.solon.ai.chat.functioncall.ChatFunctionCall;
import org.noear.solon.ai.chat.functioncall.ChatFunctionParam;
import org.noear.solon.ai.chat.message.*;

import java.util.*;

/**
 * @author noear
 * @since 3.1
 */
public abstract class AbstractDialect implements ChatDialect {

    protected void buildChatMessageNodeDo(ONode oNode, AssistantMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        oNode.set("content", msg.getContent());

        if (Utils.isNotEmpty(msg.getReasoningContent())) {
            oNode.set("reasoning_content", msg.getReasoningContent());
        }

        if (msg.getToolCallsRaw() != null) {
            oNode.set("tool_calls", msg.getToolCallsRaw());
        }
    }

    protected void buildChatMessageNodeDo(ONode oNode, SystemMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        oNode.set("content", msg.getContent());
    }

    protected void buildChatMessageNodeDo(ONode oNode, ToolMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        oNode.set("content", msg.getContent());

        if (Utils.isNotEmpty(msg.getName())) {
            oNode.set("name", msg.getName());
        }

        if (Utils.isNotEmpty(msg.getId())) {
            oNode.set("tool_call_id", msg.getId());
        }
    }

    protected void buildChatMessageNodeDo(ONode oNode, UserMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        if (Utils.isEmpty(msg.getImageUrls())) {
            oNode.set("content", msg.getContent());
        } else {
            oNode.getOrNew("content").build(n1 -> {
                for (String imgUrl : msg.getImageUrls()) {
                    ONode n2 = n1.addNew();
                    n2.set("type", "image_url");

                    if (imgUrl.contains("://")) {
                        //url
                        n2.getOrNew("image_url").set("url", imgUrl);
                    } else if (imgUrl.contains(",")) {
                        //image base64
                        n2.getOrNew("image_url").set("url", imgUrl);
                    } else {
                        //base64
                        n2.getOrNew("image_url").set("url", "data:image/jpeg;base64," + imgUrl);
                    }
                }

                n1.addNew().set("type", "text").set("text", msg.getContent());
            });
        }
    }

    public ONode buildChatMessageNode(ChatMessage chatMessage) {
        ONode oNode = new ONode();
        if (chatMessage instanceof AssistantMessage) {
            buildChatMessageNodeDo(oNode, (AssistantMessage) chatMessage);
        } else if (chatMessage instanceof SystemMessage) {
            buildChatMessageNodeDo(oNode, (SystemMessage) chatMessage);
        } else if (chatMessage instanceof ToolMessage) {
            buildChatMessageNodeDo(oNode, (ToolMessage) chatMessage);
        } else if (chatMessage instanceof UserMessage) {
            buildChatMessageNodeDo(oNode, (UserMessage) chatMessage);
        } else {
            throw new IllegalArgumentException("Unsupported chat message type: " + chatMessage.getClass());
        }

        return oNode;
    }

    protected void buildReqFunctionsNode(ONode n, ChatConfig config, ChatOptions options, ChatMessage lastMessage) {
        buildReqFunctionsNodeDo(n, config.globalFunctions());
        buildReqFunctionsNodeDo(n, options.functions());
    }

    protected void buildReqFunctionsNodeDo(ONode n, Collection<ChatFunction> funcs) {
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
    public String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (Utils.isNotEmpty(config.model())) {
                n.set("model", config.model());
            }

            n.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    n1.add(buildChatMessageNode(m1));
                }
            });

            for (Map.Entry<String, Object> kv : options.options().entrySet()) {
                n.set(kv.getKey(), kv.getValue());
            }

            ChatMessage lastMessage = messages.get(messages.size() - 1);
            buildReqFunctionsNode(n, config, options, lastMessage);
        }).toJson();
    }

    protected List<ChatFunctionCall> parseToolCalls(ONode toolCallsNode) {
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

    protected AssistantMessage parseAssistantMessage(ONode oMessage) {
        String content = oMessage.get("content").getString();
        String reasoning_content = oMessage.get("reasoning_content").getString();
        ONode toolCallsNode = oMessage.getOrNull("tool_calls");
        List<ChatFunctionCall> toolCalls = parseToolCalls(toolCallsNode);

        return new AssistantMessage(content, reasoning_content, toolCallsNode, toolCalls);
    }
}