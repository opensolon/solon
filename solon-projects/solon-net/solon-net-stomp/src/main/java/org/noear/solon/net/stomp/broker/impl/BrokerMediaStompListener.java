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
package org.noear.solon.net.stomp.broker.impl;

import org.noear.solon.core.util.KeyValue;
import org.noear.solon.net.stomp.*;
import org.noear.solon.net.stomp.listener.StompListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Stomp 代理中介监听器（收集协议元数据）
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class BrokerMediaStompListener implements StompListener {
    static final Logger log = LoggerFactory.getLogger(BrokerMediaStompListener.class);

    private final StompBrokerMedia brokerMedia;

    public BrokerMediaStompListener(StompBrokerMedia brokerMedia) {
        this.brokerMedia = brokerMedia;
    }

    /**
     * 可以放鉴权，参数可以通过Head或者地址栏
     * 鉴权失败可以直接关闭, 如：socket.close();
     *
     * @param session
     */
    @Override
    public void onOpen(StompSession session) {
        brokerMedia.sessionIdMap.put(session.id(), session);

        if (session.name() != null) {
            brokerMedia.sessionNameMap.computeIfAbsent(session.name(), k -> new CopyOnWriteArrayList<>())
                    .add(session);
        }
    }


    /**
     * 连接关闭
     * 当连接断开时触发
     */
    @Override
    public void onClose(StompSession session) {
        brokerMedia.sessionIdMap.remove(session.id());

        if (session.name() != null) {
            List<StompSession> sessionList = brokerMedia.sessionNameMap.get(session.name());
            if (sessionList != null) {
                sessionList.remove(session);
                if (sessionList.size() == 0) {
                    brokerMedia.sessionNameMap.remove(session.name());
                }
            }
        }

        this.onUnsubscribe(session, null);
    }

    @Override
    public void onFrame(StompSession session, Frame frame) {
        switch (frame.getCommand()) {
            case Commands.STOMP:
            case Commands.CONNECT: {
                onConnect(session, frame);
                break;
            }
            case Commands.DISCONNECT: {
                onDisconnect(session, frame);
                break;
            }
            case Commands.SUBSCRIBE: {
                onSubscribe(session, frame);
                break;
            }
            case Commands.UNSUBSCRIBE: {
                onUnsubscribe(session, frame);
                break;
            }
            case Commands.SEND: {
                onSend(session, frame);
                break;
            }
            case Commands.ACK:
            case Commands.NACK: {
                onAck(session, frame);
                break;
            }
            default: {
                //未知命令
                log.warn("Frame unknown, {}\r\n{}", session.id(), frame.getSource());

                session.send(Frame.newBuilder().command(Commands.UNKNOWN).payload(frame.getSource()).build());
            }
        }
    }

    @Override
    public void onError(StompSession socket, Throwable error) {

    }

    /**
     * 连接命令
     * 需要响应
     */
    public void onConnect(StompSession session, Frame frame) {
        String heartBeat = frame.getHeader(Headers.HEART_BEAT);

        Frame frame1 = Frame.newBuilder().command(Commands.CONNECTED)
                .headerAdd(new KeyValue<>(Headers.HEART_BEAT, (heartBeat == null ? "0,0" : heartBeat)),
                        new KeyValue<>(Headers.SERVER, "stomp"),
                        new KeyValue<>(Headers.VERSION, "1.2"))
                .build();

        session.send(frame1);
    }

    /**
     * 断开命令
     * 需要响应
     */
    public void onDisconnect(StompSession session, Frame frame) {
        //((StompSessionImpl) session).receipt(frame);
    }

    /**
     * 订阅命令
     */
    public void onSubscribe(StompSession session, Frame frame) {
        //订阅者Id
        final String subscriptionId = frame.getHeader(Headers.ID);
        //目的地
        final String destination = frame.getHeader(Headers.DESTINATION);

        if (destination == null || destination.length() == 0 || subscriptionId == null || subscriptionId.length() == 0) {
            Frame frame1 = Frame.newBuilder().command(Commands.ERROR)
                    .payload("Required 'destination' or 'id' header missed")
                    .build();

            session.send(frame1);
            return;
        }

        Subscription subscription = new Subscription(session.id(), destination, subscriptionId);

        ((StompSessionImpl) session).addSubscription(subscription);
        brokerMedia.subscriptions.add(subscription);

        //((StompSessionImpl) session).receipt(frame);
    }


    /**
     * 取消订阅命令
     */
    public void onUnsubscribe(StompSession socket, Frame frame) {
        final String sessionId = socket.id();
        if (frame == null) {
            this.unSubscribeHandle(destinationInfo -> {
                return sessionId.equals(destinationInfo.getSessionId());
            });
        } else {
            String subscriptionId = frame.getHeader(Headers.ID);
            String destination = frame.getHeader(Headers.DESTINATION);

            this.unSubscribeHandle(subscription -> {
                return sessionId.equals(subscription.getSessionId())
                        && (subscription.getDestination().equals(destination) || subscription.getId().equals(subscriptionId));
            });
        }
    }

    /**
     * 发送消息
     */
    public void onSend(StompSession session, Frame frame) {

    }

    /**
     * 消息ACK
     */
    public void onAck(StompSession session, Frame frame) {

    }

    /**
     * 删除订阅
     *
     * @param function
     */
    protected void unSubscribeHandle(Function<Subscription, Boolean> function) {
        Iterator<Subscription> iterator = brokerMedia.subscriptions.iterator();

        while (iterator.hasNext()) {
            if (function.apply(iterator.next())) {
                iterator.remove();
            }
        }
    }
}