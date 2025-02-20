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
package org.noear.solon.ai.chat;

import org.noear.solon.ai.AiConfig;
import org.noear.solon.ai.chat.function.ChatFunction;

import java.util.*;

/**
 * 聊天模型配置
 *
 * @author noear
 * @since 3.1
 */
public class ChatConfig extends AiConfig {
    private final Map<String, ChatFunction> globalFunctions = new LinkedHashMap<>();

    /**
     * 添加全局函数
     */
    public void addGlobalFunction(ChatFunction function) {
        globalFunctions.put(function.name(), function);
    }

    /**
     * 获取单个全局函数
     */
    public ChatFunction getGlobalFunction(String name) {
        return globalFunctions.get(name);
    }

    /**
     * 获取所有全局函数
     */
    public Collection<ChatFunction> getGlobalFunctions() {
        return globalFunctions.values();
    }
}