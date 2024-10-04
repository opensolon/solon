/*
 * Copyright 2017-2024 noear.org and authors
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
package org.noear.solon.net.stomp;


import org.noear.solon.core.util.KeyValue;

import java.util.Collection;


/**
 * 消息
 *
 * @author limliu
 * @since 2.7
 */
public interface Message {
    /**
     * 获取命令, 如send...等。参考#Commands
     */
    String getCommand();

    /**
     * 获取消息内容
     *
     * @return
     */
    String getPayload();

    /**
     * 获取head
     *
     * @param key 参考#Header
     */
    String getHeader(String key);

    /**
     * 获取head集合
     *
     * @return
     */
    Collection<KeyValue<String>> getHeaderAll();

    static MessageBuilder newBuilder() {
        return new MessageBuilder();
    }
}