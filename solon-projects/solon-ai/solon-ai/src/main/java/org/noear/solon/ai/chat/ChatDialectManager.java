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
import org.noear.solon.core.util.RankEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聊天方言管理
 *
 * @author noear
 */
public class ChatDialectManager {
    private static List<RankEntity<ChatDialect>> dialects = new ArrayList<>();

    static {
        add(OllamaDialect.getInstance());
    }

    /**
     * 获取方言
     */
    public static ChatDialect get(ChatConfig config) {
        for (RankEntity<ChatDialect> d : dialects) {
            if (d.target.matched(config)) {
                return d.target;
            }
        }

        return OpenaiDialect.instance();
    }

    /**
     * 添加方言
     */
    public static void add(ChatDialect dialect) {
        add(dialect, 0);
    }

    /**
     * 添加方言
     */
    public static void add(ChatDialect dialect, int index) {
        dialects.add(new RankEntity<>(dialect, index));
        Collections.sort(dialects);
    }

    /**
     * 移除方言
     */
    public static void remove(ChatDialect dialect) {
        dialects.removeIf(rankEntity -> rankEntity.target == dialect);
    }
}