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
import org.noear.solon.ai.AiMedia;
import org.noear.solon.ai.audio.Audio;
import org.noear.solon.ai.chat.*;
import org.noear.solon.ai.chat.function.ChatFunction;
import org.noear.solon.ai.chat.function.ChatFunctionCall;
import org.noear.solon.ai.chat.function.ChatFunctionParam;
import org.noear.solon.ai.chat.message.*;
import org.noear.solon.ai.image.Image;
import org.noear.solon.ai.video.Video;

import java.util.*;

/**
 * 聊天模型方言虚拟类
 *
 * @author noear
 * @since 3.1
 */
public abstract class AbstractChatDialect implements ChatDialect {

    protected void buildChatMessageNodeDo(ONode oNode, AssistantMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());

        oNode.set("content", msg.getResultContent());

        if (Utils.isNotEmpty(msg.getToolCallsRaw())) {
            oNode.set("tool_calls", ONode.load(msg.getToolCallsRaw()));
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

        if (Utils.isNotEmpty(msg.getToolCallId())) {
            oNode.set("tool_call_id", msg.getToolCallId());
        }
    }

    protected void buildChatMessageNodeDo(ONode oNode, UserMessage msg) {
        oNode.set("role", msg.getRole().name().toLowerCase());
        if (Utils.isEmpty(msg.getMedias())) {
            oNode.set("content", msg.getContent());
        } else {
            oNode.getOrNew("content").build(n1 -> {
                n1.addNew().set("type", "text").set("text", msg.getContent());

                for (AiMedia media : msg.getMedias()) {
                    if (media instanceof Image) {
                        n1.addNew().set("type", "image_url").getOrNew("image_url").set("url", media.toDataString(true));
                    } else if (media instanceof Audio) {
                        n1.addNew().set("type", "audio_url").getOrNew("audio_url").set("url", media.getUrl());
                    } else if (media instanceof Video) {
                        n1.addNew().set("type", "video_url").getOrNew("video_url").set("url", media.getUrl());
                    }
                }
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
        buildReqFunctionsNodeDo(n, config.getGlobalFunctions());
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
                                        buildReqFunctionParamNodeDo(p1, n6);
                                    });

                                    if (p1.required()) {
                                        n4r.add(p1.name());
                                    }
                                }
                            });
                        });
                    });
                });
            }
        });
    }

    /**
     * 字符串形态
     */
    protected void buildReqFunctionParamNodeDo(ChatFunctionParam p1, ONode n6) {
        String typeStr = p1.type().getSimpleName().toLowerCase();
        if (p1.type().isArray()) {
            n6.set("type", "array");
            String typeItem = typeStr.substring(0, typeStr.length() - 2); //int[]

            n6.getOrNew("items").set("type", typeItem);
        } else if (p1.type().isEnum()) {
            n6.set("type", typeStr);

            n6.getOrNew("enum").build(n7 -> {
                for (Object e : p1.type().getEnumConstants()) {
                    n7.add(e.toString());
                }
            });
        } else {
            n6.set("type", typeStr);
        }

        n6.set("description", p1.description());
    }

    @Override
    public String buildRequestJson(ChatConfig config, ChatOptions options, List<ChatMessage> messages, boolean stream) {
        return new ONode().build(n -> {
            n.set("stream", stream);

            if (Utils.isNotEmpty(config.getModel())) {
                n.set("model", config.getModel());
            }

            n.getOrNew("messages").build(n1 -> {
                for (ChatMessage m1 : messages) {
                    if (m1.isThinking() == false) {
                        n1.add(buildChatMessageNode(m1));
                    }
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

            Map<String, Object> args = n1fArgs.toObject(Map.class);
            toolCalls.add(new ChatFunctionCall(callId, name, args));
        }

        return toolCalls;
    }

    protected List<AssistantMessage> parseAssistantMessage(boolean isStream, ChatResponseDefault resp, ONode oMessage) {
        List<AssistantMessage> messageList = new ArrayList<>();

        String content = oMessage.get("content").getRawString();
        ONode toolCallsNode = oMessage.getOrNull("tool_calls");

        List<Map> toolCallsRaw = null;
        List<ChatFunctionCall> toolCalls = parseToolCalls(toolCallsNode);

        if (Utils.isNotEmpty(toolCalls)) {
            toolCallsRaw = toolCallsNode.toObject(List.class);
        }

        if (oMessage.contains("reasoning_content")) {
            //有思考专属内容的协议
            String reasoning_content = oMessage.get("reasoning_content").getRawString();

            if (isStream) {
                //如果是流返回（可能要拆成多条流消息）
                if (content == null) {
                    if (resp.reasoning == false) {
                        //说明是第一次
                        messageList.add(new AssistantMessage("<think>", true, null, null));
                        messageList.add(new AssistantMessage("\n\n", true, null, null));
                        if (Utils.isNotEmpty(reasoning_content)) {
                            content = reasoning_content;
                        }
                    } else {
                        content = reasoning_content;
                    }

                    resp.reasoning = true;
                } else {
                    if (resp.reasoning) {
                        //说明是最后一次
                        messageList.add(new AssistantMessage("</think>", true, null, null));
                        messageList.add(new AssistantMessage("\n\n", false, null, null));
                    }

                    resp.reasoning = false;
                }
            } else {
                //如查是单次返回
                if (Utils.isNotEmpty(reasoning_content)) {
                    content = "<think>\n\n" + reasoning_content + "</think>\n\n" + content;
                }
            }
        } else {
            //分析 think 状态
            if (isStream) {
                //如果是流返回
                if (content.startsWith("<think>")) {
                    resp.reasoning = true;
                } else {
                    if (resp.reasoning) {
                        int thinkEnd = content.indexOf("</think>");
                        if (thinkEnd >= 0) { //可能是个开始符
                            resp.reasoning = false;
                            messageList.add(new AssistantMessage(content, true, null, null));
                            return messageList;
                        }
                    }
                }
            }
        }

        if (content != null) {
            messageList.add(new AssistantMessage(content, resp.reasoning, toolCallsRaw, toolCalls));
        }

        return messageList;
    }
}