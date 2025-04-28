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
package org.noear.solon.net.http.textstream;

import org.noear.solon.Utils;

import java.io.Serializable;
import java.util.Map;

/**
 * 服务端 Sse 事件文本流
 *
 * @author noear
 * @since 3.1
 */
public class ServerSentEvent implements Serializable {
    private String data;
    private String id;
    private String event;
    private String retry;

    public ServerSentEvent() {
        //用于反序列化
    }

    public ServerSentEvent(Map<String, String> meta, String data) {
        if (data != null) {
            this.data = data.trim();
        }

        if (meta != null) {
            this.id = meta.get("id");
            this.event = meta.get("event");
            this.retry = meta.get("retry");
        }
    }

    public ServerSentEvent(String id, String event, String data, String retry) {
        this.id = id;
        this.event = event;
        this.retry = retry;

        if (data != null) {
            this.data = data.trim();
        }
    }

    public String getData() {
        return data;
    }

    public String getId() {
        return id;
    }

    public String getEvent() {
        return event;
    }

    public String getRetry() {
        return retry;
    }

    /**
     * @deprecated 3.1 {@link #getId()}
     */
    @Deprecated
    public String id() {
        return id;
    }

    /**
     * @deprecated 3.1 {@link #getEvent()}
     */
    @Deprecated
    public String event() {
        return event;
    }

    /**
     * @deprecated 3.1 {@link #getData()}
     */
    @Deprecated
    public String data() {
        return this.data;
    }

    /**
     * @deprecated 3.1 {@link #getRetry()}
     */
    @Deprecated
    public String retry() {
        return this.retry;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (Utils.isNotEmpty(id)) {
            buf.append("id:").append(id).append("\n");
        }

        if (Utils.isNotEmpty(event)) {
            buf.append("event:").append(event).append("\n");
        }

        if (Utils.isNotEmpty(data)) {
            buf.append("data:").append(data).append("\n");
        }

        if (Utils.isNotEmpty(retry)) {
            buf.append("retry:").append(retry).append("\n");
        }

        return buf.toString();
    }
}