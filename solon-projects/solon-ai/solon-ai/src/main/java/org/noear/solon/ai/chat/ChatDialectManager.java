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

import org.noear.solon.ai.chat.dialect.OllamaDialect;
import org.noear.solon.ai.chat.dialect.OpenaiDialect;

import java.util.HashMap;
import java.util.Map;

/**
 * 聊天方言管理
 *
 * @author noear
 */
public class ChatDialectManager {
    private static Map<String, ChatDialect> dialects = new HashMap<>();

    static {
        add(OllamaDialect.getInstance());
        add(OpenaiDialect.instance());
    }

    /**
     * 获取
     */
    public static ChatDialect get(String name) {
        if (name == null) {
            return OpenaiDialect.instance();
        } else {
            return dialects.getOrDefault(name, OpenaiDialect.instance());
        }
    }

    /**
     * 添加
     */
    public static void add(ChatDialect dialect) {
        dialects.put(dialect.provider(), dialect);
    }

    /**
     * 移除
     */
    public static void remove(String name) {
        dialects.remove(name);
    }
}