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
package org.noear.solon.net.stomp;

import org.noear.solon.core.util.KeyValue;
import org.noear.solon.core.util.KeyValues;
import org.noear.solon.core.util.MultiMap;

/**
 * Stomp 消息
 *
 * @author noear
 * @since 3.0
 */
public class Message extends SimpleFrame implements Frame {

    public Message(String payload) {
        super(null, Commands.MESSAGE, payload, new MultiMap<>());
    }

    /**
     * 头添加
     */
    public Message headerAdd(KeyValue<String>... headers) {
        for (KeyValue<String> kv : headers) {
            this.headers.holder(kv.getKey()).addValue(kv.getValue());
        }
        return this;
    }

    /**
     * 头添加
     */
    public Message headerAdd(Iterable<KeyValues<String>> headers) {
        for (KeyValues<String> kv : headers) {
            for (String val : kv.getValues()) {
                this.headers.holder(kv.getKey()).addValue(val);
            }
        }
        return this;
    }

    /**
     * 头添加
     *
     * @param key 键名
     * @param val 值
     */
    public Message headerAdd(String key, String val) {
        if (key != null && val != null) {
            this.headers.holder(key).addValue(val);
        }

        return this;
    }

    /**
     * 头替换
     *
     * @param key 键名
     * @param val 值
     */
    public Message headerSet(String key, String val) {
        if (key != null && val != null) {
            this.headers.holder(key).setValues(val);
        }

        return this;
    }

    /**
     * 内容类型
     */
    public Message contentType(String contentType) {
        return headerSet(Headers.CONTENT_TYPE, contentType);
    }
}