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

import org.noear.solon.Utils;
import org.noear.solon.net.stomp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Stomp 代理端发射器
 *
 * @author limliu
 * @since 2.7
 * @since 3.0
 */
public class StompBrokerEmitter implements StompEmitter {
    static final Logger log = LoggerFactory.getLogger(StompBrokerEmitter.class);
    private final StompBrokerMedia brokerMedia;

    protected StompBrokerEmitter(StompBrokerMedia brokerMedia) {
        this.brokerMedia = brokerMedia;
    }

    private void sendToSessionDo(StompSession session, Subscription subscription, String destination, Message message) {
        if (subscription != null) {
            Frame replyMessage = Frame.newBuilder()
                    .command(Commands.MESSAGE)
                    .payload(message.getPayload())
                    .headerAdd(message.getHeaderAll())
                    .headerSet(Headers.DESTINATION, destination)
                    .headerSet(Headers.SUBSCRIPTION, subscription.getId())
                    .headerSet(Headers.MESSAGE_ID, Utils.guid())
                    .build();

            session.send(replyMessage);
        } else {
            log.warn("No subscription found: '{}'", destination);
        }
    }

    /**
     * 发送给会话
     *
     * @param session     会话
     * @param destination 目标
     * @param message     消息
     */
    @Override
    public void sendToSession(StompSession session, String destination, Message message) {
        Subscription subscription = ((StompSessionImpl) session).getSubscription(destination);
        sendToSessionDo(session, subscription, destination, message);
    }

    /**
     * 发送给用户
     *
     * @param user        用户
     * @param destination 目标
     * @param message     消息
     */
    @Override
    public void sendToUser(String user, String destination, Message message) {
        List<StompSession> sessions = brokerMedia.sessionNameMap.get(user);

        if (sessions == null) {
            log.warn("No user found: '{}'", user);
        } else {
            for (StompSession s1 : sessions) {
                sendToSession(s1, destination, message);
            }
        }
    }


    /**
     * 发送到目的地
     *
     * @param destination 目标
     * @param message     消息
     */
    @Override
    public void sendTo(String destination, Message message) {
        assert message != null;

        if (Utils.isEmpty(destination)) {
            return;
        }

        brokerMedia.subscriptions.parallelStream()
                .filter(subscription -> subscription.matches(destination))
                .forEach(subscription -> {
                    StompSession session = brokerMedia.sessionIdMap.get(subscription.getSessionId());

                    if (session != null) {
                        sendToSessionDo(session, subscription, destination, message);
                    }
                });
    }
}