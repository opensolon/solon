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
import org.noear.solon.net.stomp.Commands;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.StompSender;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.websocket.WebSocket;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

/**
 * Stomp 服务端帧发送器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class StompServerSender implements StompSender {
    private final StompServerOperations operations;

    protected StompServerSender(StompServerOperations operations) {
        this.operations = operations;
    }


    /**
     * 发送到会话
     *
     * @param session 会话
     * @param frame   帧
     */
    public void sendTo(WebSocket session, Frame frame) {
        assert frame != null;

        if (session.isValid()) {
            String frameStr = operations.getCodec().encode(frame);
            session.send(ByteBuffer.wrap(frameStr.getBytes(StandardCharsets.UTF_8)));
        }
    }


    /**
     * 发送到目的地
     *
     * @param destination 目标（支持模糊匹配，如/topic/**）
     * @param frame       帧
     */
    @Override
    public void sendTo(String destination, Frame frame) {
        assert frame != null;

        if (Utils.isEmpty(destination)) {
            return;
        }

        operations.getSubscriptionInfos().parallelStream()
                .filter(subscriptionInfo -> {
                    Pattern pattern = operations.getDestinationMatchs().get(subscriptionInfo.getDestination());

                    if (pattern == null) {
                        return false;
                    } else {
                        return pattern.matcher(destination).matches();
                    }
                }).forEach(subscriptionInfo -> {
                    WebSocket sendSocket = operations.getSessionMap().get(subscriptionInfo.getSessionId());

                    if (sendSocket != null) {
                        Frame replyMessage = Frame.newBuilder()
                                .command(Commands.MESSAGE)
                                .payload(frame.getPayload())
                                .header(Headers.CONTENT_TYPE, frame.getHeader(Headers.CONTENT_TYPE))
                                .header(Headers.DESTINATION, subscriptionInfo.getDestination())
                                .header(Headers.SUBSCRIPTION, subscriptionInfo.getSubscriptionId())
                                .header(Headers.MESSAGE_ID, Utils.guid())
                                .build();

                        sendTo(sendSocket, replyMessage);
                    }
                });
    }
}