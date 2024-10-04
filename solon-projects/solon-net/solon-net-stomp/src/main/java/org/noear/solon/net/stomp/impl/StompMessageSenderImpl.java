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

import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompMessageSender;
import org.noear.solon.net.websocket.WebSocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * stomp 处理工具类
 *
 * @author limliu
 * @since 2.7
 */
public final class StompMessageSenderImpl implements StompMessageSender {
    private StompMessageOperations operations = new StompMessageOperations();

    public StompMessageOperations getOperations() {
        return operations;
    }

    /**
     * 指定链接发送消息
     *
     * @param session 会话
     * @param message 消息
     */
    public void sendTo(WebSocket session, Message message) {
        if (session.isValid()) {
            session.send(ByteBuffer.wrap(operations.getMsgCodec().encode(message).getBytes(StandardCharsets.UTF_8)));
        }
    }


    /**
     * 给指定通道发送消息
     *
     * @param destination 目标，支持模糊匹配，如/topic/**
     * @param message     消息
     */
    @Override
    public void sendTo(String destination, Message message) {
        operations.getDestinationInfoSet().parallelStream()
                .filter(destinationInfo -> {
                    return operations.getDestinationMatch().get(destinationInfo.getDestination()).matcher(destination).matches();
                }).forEach(destinationInfo -> {
                    WebSocket sendSocket = operations.getWebSocketMap().get(destinationInfo.getSessionId());
                    if (sendSocket != null) {
                        sendTo(sendSocket, message);
                    }
                });
    }
}