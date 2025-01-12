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

import org.noear.solon.lang.Preview;

/**
 * Stomp 发射器
 *
 * @author noear
 * @since 3.0
 */
@Preview("3.0")
public interface StompEmitter {
    /**
     * 发送帧
     *
     * @param session     会话
     * @param destination 目的地
     * @param message     消息
     */
    void sendToSession(StompSession session, String destination, Message message);

    /**
     * 发送帧
     *
     * @param session     会话
     * @param destination 目的地
     * @param payload     消息有效核载
     */
    default void sendToSession(StompSession session, String destination, String payload) {
        sendToSession(session, destination, new Message(payload));
    }

    /**
     * 发送帧
     *
     * @param user        用户
     * @param destination 目的地
     * @param message     消息
     */
    void sendToUser(String user, String destination, Message message);

    /**
     * 发送帧
     *
     * @param user        用户
     * @param destination 目的地
     * @param payload     消息有效核载
     */
    default void sendToUser(String user, String destination, String payload) {
        sendToUser(user, destination, new Message(payload));
    }

    /**
     * 发送帧
     *
     * @param destination 目的地
     * @param message     消息
     */
    void sendTo(String destination, Message message);

    /**
     * 发送消息有效核载
     *
     * @param destination 目的地
     * @param payload     消息有效核载
     */
    default void sendTo(String destination, String payload) {
        sendTo(destination, new Message(payload));
    }
}