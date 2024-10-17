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

import java.util.*;

/**
 * Stomp 消息
 *
 * @author noear
 * @since 3.0
 */
public class Message extends SimpleFrame implements Frame {

    public Message(String payload) {
        super(null, Commands.MESSAGE, payload, new ArrayList<>());
    }

    /**
     * 配置头
     */
    public Message headers(KeyValue<String>... headers) {
        for (KeyValue<String> header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    /**
     * 配置头
     */
    public Message headers(Iterable<KeyValue<String>> headers) {
        for (KeyValue<String> header : headers) {
            this.headers.add(header);
        }
        return this;
    }

    /**
     * 配置头
     *
     * @param key 键名
     * @param val 值
     */
    public Message header(String key, String val) {
        if (key != null && val != null) {
            headers.add(new KeyValue<>(key, val));
        }

        return this;
    }
}