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
package org.noear.solon.ai.chat.message;

import org.noear.snack.ONode;
import org.noear.snack.core.Feature;
import org.noear.solon.ai.AiMedia;
import org.noear.solon.ai.chat.ChatRole;
import org.noear.solon.ai.image.Image;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息
 *
 * @author noear
 * @since 3.1
 */
public interface ChatMessage extends Serializable {
    /**
     * 角色
     */
    ChatRole getRole();

    /**
     * 内容
     */
    String getContent();

    /**
     * 获取元数据
     */
    Map<String, Object> getMetadata();

    /**
     * 添加元数据
     */
    ChatMessage addMetadata(Map<String, Object> map);

    /**
     * 添加元数据
     */
    ChatMessage addMetadata(String key, Object value);

    /**
     * 是否思考中
     */
    default boolean isThinking() {
        return false;
    }

    /// //////////////

    static AssistantMessage ofAssistant(String content) {
        return new AssistantMessage(content, false, null, null);
    }

    /**
     * 构建系统消息
     */
    static ChatMessage ofSystem(String content) {
        return new SystemMessage(content);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content) {
        return new UserMessage(content, null);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content, List<AiMedia> medias) {
        return new UserMessage(content, medias);
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content, AiMedia... medias) {
        return new UserMessage(content, Arrays.asList(medias));
    }

    /**
     * 构建用户消息
     */
    static ChatMessage ofUser(String content, Image... images) {
        return new UserMessage(content, Arrays.asList(images));
    }

    /**
     * 构建工具消息
     */
    static ChatMessage ofTool(String content, String name, String toolCallId) {
        return new ToolMessage(content, name, toolCallId);
    }

    /// //////////////////

    /**
     * 用户消息增强
     */
    static ChatMessage augment(String message, Object context) {
        String newContent = String.format("%s\n\n Now: %s\n\n References: %s", message,
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME), context);
        return new UserMessage(newContent);
    }

    /**
     * 创建用户消息模板
     */
    static UserMessageTemplate template(String tmpl) {
        return new UserMessageTemplate(tmpl);
    }

    /// //////////////////

    /**
     * 序列化为 json
     */
    static String toJson(ChatMessage message) {
        return ONode.load(message, Feature.EnumUsingName).toJson();
    }

    /**
     * 从 json 反序列化为消息
     */
    static ChatMessage fromJson(String json) {
        ONode oNode = ONode.loadStr(json);
        ChatRole role = ChatRole.valueOf(oNode.get("role").getString());

        if (role == ChatRole.TOOL) {
            return oNode.toObject(ToolMessage.class);
        } else if (role == ChatRole.SYSTEM) {
            return oNode.toObject(SystemMessage.class);
        } else if (role == ChatRole.USER) {
            return oNode.toObject(UserMessage.class);
        } else {
            return oNode.toObject(AssistantMessage.class);
        }
    }
}