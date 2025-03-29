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

    public ServerSentEvent(){
        //用于反序列化
    }

    public ServerSentEvent(Map<String, String> meta, String data) {
        this.data = data;
        this.id = meta.get("id");
        this.event = meta.get("event");
        this.retry = meta.get("retry");
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
}