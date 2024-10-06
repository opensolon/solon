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

import org.noear.solon.Utils;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.StompSender;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;

/**
 * websocket 转 stomp 适配器 (使用 ToStompWebSocketAdapter 比 ToStompWebSocketListener，扩展类更清爽些)
 *
 * @author noear
 * @since 3.0
 */
public class StompBroker implements WebSocketListenerSupplier {
    protected final ToStompWebSocketListener toStompWebSocketListener;

    public StompBroker() {
        ServerEndpoint serverEndpoint = getClass().getAnnotation(ServerEndpoint.class);
        if (serverEndpoint == null || Utils.isEmpty(serverEndpoint.value())) {
            throw new IllegalArgumentException("Endpoint is not empty");
        }

        toStompWebSocketListener = new ToStompWebSocketListener(serverEndpoint.value());
    }

    @Override
    public WebSocketListener getWebSocketListener() {
        return toStompWebSocketListener;
    }

    public void addListener(StompListener... listeners) {
        toStompWebSocketListener.addListener(listeners);
    }

    public StompSender getSender() {
        return toStompWebSocketListener.getSender();
    }
}
