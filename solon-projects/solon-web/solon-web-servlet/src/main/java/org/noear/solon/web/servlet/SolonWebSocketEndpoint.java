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
package org.noear.solon.web.servlet;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketBase;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.websocket.*;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.concurrent.Future;

/**
 * @author noear
 * @since 2.6
 */
public class SolonWebSocketEndpoint extends Endpoint {
    public static void addEndpoint(ServletContext sc) {
        ServerContainer serverContainer = (ServerContainer) sc.getAttribute("javax.websocket.server.ServerContainer");
        if (serverContainer == null) {
            throw new IllegalStateException("Missing javax.websocket.server.ServerContainer");
        }

        Collection<String> paths = WebSocketRouter.getInstance().getPaths();

        try {
            for (String path : paths) {
                if (path.startsWith("/")) {
                    ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder
                            .create(SolonWebSocketEndpoint.class, path)
                            .build();
                    serverContainer.addEndpoint(endpointConfig);
                }
            }
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }

    private final String SESSION_KEY = "session";

    private static final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        WebSocket socket = new WebSocketImpl(session);
        session.getUserProperties().put(SESSION_KEY, socket);
        session.addMessageHandler(new BufferMessageHandler(socket));
        session.addMessageHandler(new TextMessageHandler(socket));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        WebSocket socket = (WebSocket) session.getUserProperties().get(SESSION_KEY);

        webSocketRouter.getListener().onClose(socket);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        WebSocket socket = (WebSocket) session.getUserProperties().get(SESSION_KEY);

        webSocketRouter.getListener().onError(socket, thr);
    }

    private static class TextMessageHandler implements MessageHandler.Whole<String> {
        private WebSocket socket;

        public TextMessageHandler(WebSocket socket) {
            this.socket = socket;
        }

        @Override
        public void onMessage(String s) {
            try {
                webSocketRouter.getListener().onMessage(socket, s);
            } catch (Throwable e) {
                webSocketRouter.getListener().onError(socket, e);
            }
        }
    }

    private static class BufferMessageHandler implements MessageHandler.Whole<ByteBuffer> {
        private WebSocket socket;

        public BufferMessageHandler(WebSocket socket) {
            this.socket = socket;
        }

        @Override
        public void onMessage(ByteBuffer s) {
            try {
                webSocketRouter.getListener().onMessage(socket, s);
            } catch (Throwable e) {
                webSocketRouter.getListener().onError(socket, e);
            }
        }
    }

    private static class WebSocketImpl extends WebSocketBase {
        private static final Logger log = LoggerFactory.getLogger(WebSocketImpl.class);
        private Session real;

        public WebSocketImpl(Session real) {
            this.real = real;
            this.init(real.getRequestURI());
        }

        @Override
        public boolean isValid() {
            return isClosed() == false && real.isOpen();
        }

        @Override
        public boolean isSecure() {
            return real.isSecure();
        }

        @Override
        public InetSocketAddress remoteAddress() {
            return null;
        }

        @Override
        public InetSocketAddress localAddress() {
            return null;
        }

        @Override
        public long getIdleTimeout() {
            return real.getMaxIdleTimeout();
        }

        @Override
        public void setIdleTimeout(long idleTimeout) {
            real.setMaxIdleTimeout(idleTimeout);
        }


        @Override
        public Future<Void> send(String text) {
            return real.getAsyncRemote().sendText(text);
        }

        @Override
        public Future<Void> send(ByteBuffer binary) {
            return real.getAsyncRemote().sendBinary(binary);
        }

        @Override
        public void close() {
            super.close();
            try {
                real.close();
            } catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug("{}", e);
                }
            }
        }
    }
}