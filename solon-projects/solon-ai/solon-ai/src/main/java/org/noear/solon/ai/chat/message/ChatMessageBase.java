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

import org.noear.solon.Utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 聊天消息基类
 *
 * @author noear
 * @since 3.1
 */
public abstract class ChatMessageBase<Slf extends ChatMessageBase> implements ChatMessage {
    protected Map<String, Object> metadata;

    /**
     * 获取元数据
     */
    @Override
    public Map<String, Object> getMetadata() {
        if (metadata == null) {
            metadata = new LinkedHashMap<>();
        }

        return metadata;
    }

    /**
     * 添加元数据
     */
    @Override
    public Slf addMetadata(Map<String, Object> map) {
        if (Utils.isNotEmpty(map)) {
            getMetadata().putAll(map);
        }

        return (Slf) this;
    }

    /**
     * 添加元数据
     */
    @Override
    public Slf addMetadata(String key, Object value) {
        if (Utils.isNotEmpty(key)) {
            getMetadata().put(key, value);
        }

        return (Slf) this;
    }
}
