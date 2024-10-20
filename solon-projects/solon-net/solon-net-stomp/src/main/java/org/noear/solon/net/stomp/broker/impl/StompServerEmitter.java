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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.Utils;
import org.noear.solon.net.stomp.*;
import org.noear.solon.net.websocket.WebSocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Stomp 服务端发射器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class StompServerEmitter implements StompEmitter {
    private final StompServerOperations operations;

    protected StompServerEmitter(StompServerOperations operations) {
        this.operations = operations;
    }


    /**
     * 发送到会话
     *
     * @param session 会话
     * @param frame   帧
     */
    public void sendToSession(WebSocket session, Frame frame) {
        assert frame != null;

        if (session.isValid()) {
            String frameStr = operations.getCodec().encode(frame);
            session.send(ByteBuffer.wrap(frameStr.getBytes(StandardCharsets.UTF_8)));
        }
    }

    @Override
    public void sendToUser(String user, String destination, Message message) {
        WebSocket sendSocket = operations.getSessionNameMap().get(user);

        Frame replyMessage = Frame.newBuilder()
                .command(Commands.MESSAGE)
                .payload(message.getPayload())
                .headerAdd(message.getHeaderAll())
                .headerSet(Headers.DESTINATION, destination)
                .headerSet(Headers.MESSAGE_ID, Utils.guid())
                .build();

        sendToSession(sendSocket, replyMessage);
    }


    /**
     * 发送到目的地
     *
     * @param destination 目标（支持模糊匹配，如/topic/**）
     * @param message     消息
     */
    @Override
    public void sendTo(String destination, Message message) {
        assert message != null;

        if (Utils.isEmpty(destination)) {
            return;
        }

        operations.getSubscriptionInfos().parallelStream()
                .filter(subscriptionInfo -> {
                    Pattern pattern = operations.getDestinationPatterns().get(subscriptionInfo.getDestination());

                    if (pattern == null) {
                        return false;
                    } else {
                        return pattern.matcher(destination).matches();
                    }
                }).forEach(subscriptionInfo -> {
                    WebSocket sendSocket = operations.getSessionIdMap().get(subscriptionInfo.getSessionId());

                    if (sendSocket != null) {
                        Frame replyMessage = Frame.newBuilder()
                                .command(Commands.MESSAGE)
                                .payload(message.getPayload())
                                .headerAdd(message.getHeaderAll())
                                .headerSet(Headers.DESTINATION, subscriptionInfo.getDestination())
                                .headerSet(Headers.SUBSCRIPTION, subscriptionInfo.getSubscriptionId())
                                .headerSet(Headers.MESSAGE_ID, Utils.guid())
                                .build();

                        sendToSession(sendSocket, replyMessage);
                    }
                });
    }
}