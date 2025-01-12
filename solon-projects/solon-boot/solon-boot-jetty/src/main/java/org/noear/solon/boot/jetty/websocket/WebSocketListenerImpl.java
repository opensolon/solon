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
package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WebSocketPingPongListener;
import org.noear.solon.core.util.RunUtil;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class WebSocketListenerImpl extends WebSocketAdapter implements WebSocketPingPongListener {
    static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);

    private WebSocketImpl webSocket;
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);

        session.getPolicy().setMaxTextMessageBufferSize(17_000_000);
        webSocket = new WebSocketImpl(session);
        session.getUpgradeRequest().getHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                webSocket.param(k, v.get(0));
            }
        });

        webSocketRouter.getListener().onOpen(webSocket);
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(payload, offset, len);
            webSocketRouter.getListener().onMessage(webSocket, buf);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onWebSocketText(String text) {
        try {
            webSocketRouter.getListener().onMessage(webSocket, text);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        if (webSocket.isClosed()) {
            return;
        } else {
            RunUtil.runAndTry(webSocket::close);
        }

        webSocketRouter.getListener().onClose(webSocket);
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        webSocketRouter.getListener().onError(webSocket, cause);
    }

    @Override
    public void onWebSocketPing(ByteBuffer byteBuffer) {
        webSocketRouter.getListener().onPing(webSocket);
    }

    @Override
    public void onWebSocketPong(ByteBuffer byteBuffer) {
        webSocketRouter.getListener().onPong(webSocket);
    }
}
