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
import org.noear.solon.core.util.KeyValue;
import org.noear.solon.net.stomp.Commands;
import org.noear.solon.net.stomp.Frame;
import org.noear.solon.net.stomp.Headers;
import org.noear.solon.net.stomp.listener.StompListener;
import org.noear.solon.net.websocket.WebSocket;

import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * Stomp 服务端消息操作监听
 *
 * @author limliu
 * @since 2.7
 */
public class StompServerOperationsListener implements StompListener {
    private final StompServerEmitter emitter;
    private final StompServerOperations operations;

    protected StompServerOperationsListener(StompServerOperations operations, StompServerEmitter emitter) {
        this.emitter = emitter;
        this.operations = operations;
    }

    /**
     * 可以放鉴权，参数可以通过Head或者地址栏
     * 鉴权失败可以直接关闭, 如：socket.close();
     *
     * @param socket
     */
    @Override
    public void onOpen(WebSocket socket) {
        operations.getSessionMap().put(socket.id(), socket);
    }

    /**
     * 连接关闭
     * 当连接断开时触发
     */
    @Override
    public void onClose(WebSocket socket) {
        operations.getSessionMap().remove(socket);
        this.onUnsubscribe(socket, null);
    }

    /**
     * 连接命令
     * 需要响应
     */
    @Override
    public void onConnect(WebSocket socket, Frame frame) {
        String heartBeat = frame.getHeader(Headers.HEART_BEAT);

        Frame frame1 = Frame.newBuilder().command(Commands.CONNECTED)
                .headers(new KeyValue<>(Headers.HEART_BEAT, (heartBeat == null ? "0,0" : heartBeat)),
                        new KeyValue<>(Headers.SERVER, "stomp"),
                        new KeyValue<>(Headers.VERSION, "1.2"))
                .build();

        emitter.sendTo(socket, frame1);
    }

    /**
     * 断开命令
     * 需要响应
     */
    @Override
    public void onDisconnect(WebSocket socket, Frame frame) {
        String receiptId = frame.getHeader(Headers.RECEIPT);

        Frame frame1 = Frame.newBuilder().command(Commands.RECEIPT)
                .header(Headers.RECEIPT_ID, receiptId)
                .build();

        emitter.sendTo(socket, frame1);
    }

    /**
     * 订阅命令
     */
    @Override
    public void onSubscribe(WebSocket socket, Frame frame) {
        //订阅者Id
        final String subscriptionId = frame.getHeader(Headers.ID);
        //目的地
        final String destination = frame.getHeader(Headers.DESTINATION);

        if (destination == null || destination.length() == 0 || subscriptionId == null || subscriptionId.length() == 0) {
            Frame frame1 = Frame.newBuilder().command(Commands.ERROR)
                    .payload("Required 'destination' or 'id' header missed")
                    .build();

            emitter.sendTo(socket, frame1);
            return;
        }

        SubscriptionInfo destinationInfo = new SubscriptionInfo(socket.id(), destination, subscriptionId);

        operations.getSubscriptionInfos().add(destinationInfo);
        if (!operations.getDestinationMatchs().containsKey(destination)) {
            String destinationRegexp = "^" + destination
                    .replaceAll("\\*\\*", ".+")
                    .replaceAll("\\*", "[^/]+") + "$";
            operations.getDestinationMatchs().put(destination, Pattern.compile(destinationRegexp));
        }

        final String receiptId = frame.getHeader(Headers.RECEIPT);
        if (receiptId != null) {
            Frame frame1 = Frame.newBuilder().command(Commands.RECEIPT)
                    .header(Headers.RECEIPT_ID, receiptId)
                    .build();
            emitter.sendTo(socket, frame1);
        }
    }


    /**
     * 取消订阅命令
     */
    @Override
    public void onUnsubscribe(WebSocket socket, Frame frame) {
        final String sessionId = socket.id();
        if (frame == null) {
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId());
            });
        } else {
            String subscriptionId = frame.getHeader(Headers.ID);
            String destination = frame.getHeader(Headers.DESTINATION);
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId())
                        && (destinationInfo.getDestination().equals(destination) || destinationInfo.getSubscriptionId().equals(subscriptionId));
            });
        }
    }

    /**
     * 发送消息
     */
    @Override
    public void onSend(WebSocket socket, Frame frame) {
        String destination = frame.getHeader(Headers.DESTINATION);

        if (Utils.isEmpty(destination)) {
            Frame frame1 = Frame.newBuilder()
                    .command(Commands.ERROR)
                    .payload("Required 'destination' header missed")
                    .build();

            emitter.sendTo(socket, frame1);
        } else {
            emitter.sendTo(destination, frame);
        }
    }

    /**
     * 消息ACK
     */
    @Override
    public void onAck(WebSocket socket, Frame frame) {

    }

    /**
     * 删除订阅
     *
     * @param function
     */
    protected void unSubscribeHandle(Function<SubscriptionInfo, Boolean> function) {
        Iterator<SubscriptionInfo> iterator = operations.getSubscriptionInfos().iterator();

        while (iterator.hasNext()) {
            if (function.apply(iterator.next())) {
                iterator.remove();
            }
        }
    }
}