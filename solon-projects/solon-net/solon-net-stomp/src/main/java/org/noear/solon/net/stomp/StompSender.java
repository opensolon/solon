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

import org.noear.solon.net.websocket.WebSocket;

/**
 * 消息发送器
 *
 * @author noear
 * @since 3.0
 */
public interface StompSender {
    void sendTo(WebSocket session, Message message);

    default void sendTo(WebSocket session, String payload) {
        sendTo(session, Message.newBuilder().payload(payload).build());
    }

    void sendTo(String destination, Message message);

    default void sendTo(String destination, String payload) {
        sendTo(destination, Message.newBuilder().payload(payload).build());
    }
}
