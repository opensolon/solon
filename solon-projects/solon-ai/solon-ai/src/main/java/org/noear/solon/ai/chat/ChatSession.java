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

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.ai.chat.message.ChatMessage;
import org.noear.solon.lang.Preview;

import java.io.*;
import java.util.List;

/**
 * 聊天会话（方便持久化）
 *
 * @author noear
 * @since 3.1
 */
@Preview("3.1")
public interface ChatSession {
    /**
     * 获取会话id
     */
    String getSessionId();

    /**
     * 获取所有消息
     */
    List<ChatMessage> getMessages();

    /**
     * 添加消息
     */
    void addMessage(ChatMessage... messages);

    /**
     * 清空消息
     */
    void clear();


    /// //////////////////////////////////////

    /**
     * 转为 ndjson
     */
    default String toNdjson() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        toNdjson(out);
        return new String(out.toByteArray(), Solon.encoding());
    }

    /**
     * 转为 ndjson
     */
    default void toNdjson(OutputStream out) throws IOException {
        for (ChatMessage msg : getMessages()) {
            out.write(ChatMessage.toJson(msg).getBytes(Solon.encoding()));
            out.write("\n".getBytes(Solon.encoding()));
            out.flush();
        }
    }

    /**
     * 加载 ndjson
     */
    default void loadNdjson(String ndjson) throws IOException {
        loadNdjson(new ByteArrayInputStream(ndjson.getBytes(Solon.encoding())));
    }

    /**
     * 加载 ndjson
     */
    default void loadNdjson(InputStream ins) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(ins))) {
            while (true) {
                String json = reader.readLine();

                if (Utils.isEmpty(json)) {
                    break;
                } else {
                    addMessage(ChatMessage.fromJson(json));
                }
            }
        }
    }
}
