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
package org.noear.solon.net.stomp.broker;

import org.noear.solon.Solon;
import org.noear.solon.Utils;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.StompSender;
import org.noear.solon.net.stomp.broker.impl.StompBrokerMedia;
import org.noear.solon.net.stomp.broker.listener.StompServerListener;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;

/**
 * Stomp 经理人
 *
 * @author noear
 * @since 3.0
 */
public class StompBroker implements WebSocketListenerSupplier {
    //WebSocket 监听器
    protected final ToStompWebSocketListener webSocketListener;
    //服务端监听器
    protected final StompBrokerMedia brokerMedia;

    public StompBroker() {
        ServerEndpoint serverEndpoint = getClass().getAnnotation(ServerEndpoint.class);
        if (serverEndpoint == null || Utils.isEmpty(serverEndpoint.value())) {
            throw new IllegalArgumentException("Endpoint is not empty");
        }

        brokerMedia = new StompBrokerMedia();
        webSocketListener = new ToStompWebSocketListener(serverEndpoint.value(), brokerMedia);

        //注册到容器
        BeanWrap bw = Solon.context().wrap(serverEndpoint.value(), brokerMedia.sender);
        Solon.context().putWrap(serverEndpoint.value(), bw);
        Solon.context().putWrap(StompSender.class, bw);
    }

    @Override
    public WebSocketListener getWebSocketListener() {
        return webSocketListener;
    }

    /**
     * 添加服务端监听器
     */
    public void addServerListener(StompServerListener... listeners) {
        for (StompServerListener listener : listeners) {
            brokerMedia.listeners.add(listener);
        }
    }

    /**
     * 获取服务端发送器
     */
    public StompSender getServerSender() {
        return brokerMedia.sender;
    }
}