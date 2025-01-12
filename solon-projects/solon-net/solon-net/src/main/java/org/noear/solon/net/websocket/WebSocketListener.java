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
package org.noear.solon.net.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * WebSoskcet 监听器
 *
 * @author noear
 * @since 2.6
 */
public interface WebSocketListener {
    /**
     * 连接打开时（可以做个签权）
     */
    void onOpen(WebSocket socket);

    /**
     * 收到消息时
     */
    void onMessage(WebSocket socket, String text) throws IOException;

    /**
     * 收到消息时
     */
    void onMessage(WebSocket socket, ByteBuffer binary) throws IOException;

    /**
     * 连接关闭时
     */
    void onClose(WebSocket socket);

    /**
     * 出错时
     */
    void onError(WebSocket socket, Throwable error);

    /**
     * Ping 时
     */
    default void onPing(WebSocket socket) {
    }

    /**
     * Pong 时
     */
    default void onPong(WebSocket socket) {
    }
}
