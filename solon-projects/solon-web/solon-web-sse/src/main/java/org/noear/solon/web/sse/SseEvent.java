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
package org.noear.solon.web.sse;

/**
 * Sse 事件
 *
 * @author kongweiguang
 * @since 2.3
 */
public class SseEvent {

    private final StringBuilder buf = new StringBuilder();

    /**
     * 添加 SSE "id" 行.
     */
    public SseEvent id(String id) {
        append("id:").append(id).append("\n");
        return this;
    }

    /**
     * 添加 SSE "event" 行.
     */
    public SseEvent name(String name) {
        append("event:").append(name).append("\n");
        return this;
    }

    /**
     * 添加 SSE "retry" 行.
     */
    public SseEvent reconnectTime(long reconnectTimeMillis) {
        append("retry:").append(String.valueOf(reconnectTimeMillis)).append("\n");
        return this;
    }

    /**
     * 添加 SSE "data" 行.
     */
    public SseEvent data(Object object) {
        append("data:").append(object.toString()).append("\n");
        return this;
    }


    /**
     * 构建为事件文本
     *
     * @deprecated 3.1 {@link #toString()}
     */
    @Deprecated
    public String build() {
        return toString();
    }

    @Override
    public String toString() {
        return buf.toString();
    }

    SseEvent append(String text) {
        this.buf.append(text);
        return this;
    }
}