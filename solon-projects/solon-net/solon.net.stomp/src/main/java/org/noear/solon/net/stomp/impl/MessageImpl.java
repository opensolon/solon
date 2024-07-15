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
package org.noear.solon.net.stomp.impl;

import org.noear.solon.net.stomp.Header;
import org.noear.solon.net.stomp.Message;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author limliu
 * @since 2.7
 */
public class MessageImpl implements Message {
    private final String command;
    private final List<Header> headers;
    private final String payload;

    public MessageImpl(String command, String payload) {
        this(command, null, payload);
    }

    public MessageImpl(String command, List<Header> headers) {
        this(command, headers, null);
    }

    public MessageImpl(String command, List<Header> headers, String payload) {
        this.command = command;
        this.headers = (headers == null) ? new ArrayList<>() : headers;
        this.payload = payload;
    }

    public Message addHeader(String key, String val) {
        this.headers.add(new Header(key, val));
        return this;
    }

    public List<Header> getHeaderAll() {
        return headers;
    }

    public String getHeader(String key) {
        if (headers == null) {
            return null;
        }

        Iterator<Header> iterator = headers.iterator();
        while (iterator.hasNext()) {
            Header h = iterator.next();
            if (key.equals(h.getKey())) {
                return h.getValue();
            }
        }

        return null;
    }

    public String getPayload() {
        return payload;
    }

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "Message {command='" + command + "', headers=" + headers + ", payload='" + payload + "'}";
    }
}
