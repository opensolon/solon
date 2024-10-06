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
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.broker.StompListener;
import org.noear.solon.net.websocket.WebSocket;

import java.util.Iterator;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * 消息处理监听
 *
 * @author limliu
 * @since 2.7
 */
public final class StompBrokerListener implements StompListener {
    private final StompBrokerSender sender;

    public StompBrokerSender getSender() {
        return sender;
    }

    public StompBrokerListener(StompBrokerSender sender) {
        this.sender = sender;
    }

    /**
     * 可以放鉴权，参数可以通过Head或者地址栏
     * 鉴权失败可以直接关闭, 如：socket.close();
     *
     * @param socket
     */
    @Override
    public void onOpen(WebSocket socket) {
        sender.getOperations().getWebSocketMap().put(socket.id(), socket);
    }

    /**
     * 连接关闭
     * 当连接断开时触发
     *
     * @param socket
     */
    @Override
    public void onClose(WebSocket socket) {
        sender.getOperations().getWebSocketMap().remove(socket);
        this.onUnsubscribe(socket, null);
    }

    /**
     * 连接命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    @Override
    public void onConnect(WebSocket socket, Message message) {
        String heartBeat = message.getHeader(Headers.HEART_BEAT);

        Message message1 = Message.newBuilder().command(Commands.CONNECTED)
                .headers(new KeyValue<>(Headers.HEART_BEAT, (heartBeat == null ? "0,0" : heartBeat)),
                        new KeyValue<>(Headers.SERVER, "stomp"),
                        new KeyValue<>(Headers.VERSION, "1.2"))
                .build();

        sender.sendTo(socket, message1);
    }

    /**
     * 断开命令
     * 需要响应
     *
     * @param socket
     * @param message
     */
    @Override
    public void onDisconnect(WebSocket socket, Message message) {
        String receiptId = message.getHeader(Headers.RECEIPT);

        Message message1 = Message.newBuilder().command(Commands.RECEIPT)
                .header(Headers.RECEIPT_ID, receiptId)
                .build();

        sender.sendTo(socket, message1);
    }

    /**
     * 订阅命令
     *
     * @param socket
     * @param message
     */
    @Override
    public void onSubscribe(WebSocket socket, Message message) {
        //订阅者Id
        final String subscriptionId = message.getHeader(Headers.ID);
        //目的地
        final String destination = message.getHeader(Headers.DESTINATION);

        if (destination == null || destination.length() == 0 || subscriptionId == null || subscriptionId.length() == 0) {
            Message message1 = Message.newBuilder().command(Commands.ERROR)
                    .payload("Required 'destination' or 'id' header missed")
                    .build();

            sender.sendTo(socket, message1);
            return;
        }

        DestinationInfo destinationInfo = new DestinationInfo(socket.id(), destination, subscriptionId);

        sender.getOperations().getDestinationInfoSet().add(destinationInfo);
        if (!sender.getOperations().getDestinationMatch().containsKey(destination)) {
            String destinationRegexp = "^" + destination.replaceAll("\\*\\*", ".+").replaceAll("\\*", ".+") + "$";
            sender.getOperations().getDestinationMatch().put(destination, Pattern.compile(destinationRegexp));
        }

        final String receiptId = message.getHeader(Headers.RECEIPT);
        if (receiptId != null) {
            Message message1 = Message.newBuilder().command(Commands.RECEIPT)
                    .header(Headers.RECEIPT_ID, receiptId)
                    .build();
            sender.sendTo(socket, message1);
        }
    }


    /**
     * 取消订阅命令
     *
     * @param socket
     * @param message
     */
    @Override
    public void onUnsubscribe(WebSocket socket, Message message) {
        final String sessionId = socket.id();
        if (message == null) {
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId());
            });
        } else {
            String subscription = message.getHeader(Headers.ID);
            String destination = message.getHeader(Headers.DESTINATION);
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId())
                        && (destinationInfo.getDestination().equals(destination) || destinationInfo.getSubscription().equals(subscription));
            });
        }
    }

    /**
     * 发送消息
     *
     * @param socket
     * @param message
     */
    @Override
    public void onSend(WebSocket socket, Message message) {
        String destination = message.getHeader(Headers.DESTINATION);

        if (Utils.isEmpty(destination)) {
            Message message1 = Message.newBuilder()
                    .command(Commands.ERROR)
                    .payload("Required 'destination' header missed")
                    .build();

            sender.sendTo(socket, message1);
        } else {
            sender.sendTo(destination, message);
        }
    }

    /**
     * 消息ACK
     *
     * @param socket
     * @param message
     */
    @Override
    public void onAck(WebSocket socket, Message message) {

    }

    /**
     * 删除订阅
     *
     * @param function
     */
    protected void unSubscribeHandle(Function<DestinationInfo, Boolean> function) {
        Iterator<DestinationInfo> iterator = sender.getOperations().getDestinationInfoSet().iterator();

        while (iterator.hasNext()) {
            if (function.apply(iterator.next())) {
                iterator.remove();
            }
        }
    }
}