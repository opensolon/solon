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

import org.noear.solon.Utils;
import org.noear.solon.core.util.KeyValue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Stomp 简单帧
 *
 * @author noear
 * @since 3.0
 */
class SimpleFrame implements Frame {
    protected final String source;
    protected final String command;
    protected final List<KeyValue<String>> headers;
    protected final String payload;

    public SimpleFrame(String source,String command, String payload, List<KeyValue<String>> headers) {
        if (Utils.isEmpty(command)) {
            this.command = Commands.MESSAGE;
        } else {
            this.command = command;
        }

        this.source = source;
        this.payload = payload;
        this.headers = headers;
    }

    /**
     * 获取原始数据
     */
    @Override
    public String getSource() {
        return source;
    }

    /**
     * 获取命令
     */
    @Override
    public String getCommand() {
        return command;
    }

    /**
     * 获取有效核载
     */
    @Override
    public String getPayload() {
        return payload;
    }

    /**
     * 获取头
     *
     * @param key 键名
     */
    @Override
    public String getHeader(String key) {
        if (headers == null) {
            return null;
        }

        Iterator<KeyValue<String>> iterator = headers.iterator();
        while (iterator.hasNext()) {
            KeyValue<String> h = iterator.next();
            if (key.equals(h.getKey())) {
                return h.getValue();
            }
        }

        return null;
    }

    /**
     * 获取所有头
     */
    @Override
    public Collection<KeyValue<String>> getHeaderAll() {
        return headers;
    }

    @Override
    public String toString() {
        return "Frame{" +
                "command='" + command + '\'' +
                ", headers=" + headers +
                ", payload='" + payload + '\'' +
                '}';
    }
}