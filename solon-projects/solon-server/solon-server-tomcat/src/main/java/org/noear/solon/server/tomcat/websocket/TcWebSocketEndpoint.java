package org.noear.solon.server.tomcat.websocket;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.concurrent.Future;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketBase;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tomcat WebSocket端点实现
 *
 */
public class TcWebSocketEndpoint extends Endpoint {
    private final String SESSION_KEY = "session";
    private static final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        WebSocket socket = new WebSocketImpl(session);
        session.getUserProperties().put(SESSION_KEY, socket);
        session.addMessageHandler(new BufferMessageHandler(socket));
        session.addMessageHandler(new TextMessageHandler(socket));
        
        // 触发onOpen事件
        webSocketRouter.getListener().onOpen(socket);
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        WebSocket socket = (WebSocket) session.getUserProperties().get(SESSION_KEY);
        if (socket != null) {
            webSocketRouter.getListener().onClose(socket);
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        WebSocket socket = (WebSocket) session.getUserProperties().get(SESSION_KEY);
        if (socket != null) {
            webSocketRouter.getListener().onError(socket, thr);
        }
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
            // Tomcat的Session不直接提供远程地址，这里返回null
            return null;
        }

        @Override
        public InetSocketAddress localAddress() {
            // Tomcat的Session不直接提供本地地址，这里返回null
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
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }

        @Override
        public void close(int code, String reason) {
            super.close(code, reason);
            try {
                real.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(code), reason));
            } catch (Throwable ignore) {
                if (log.isDebugEnabled()) {
                    log.debug("Close failure: {}", ignore.getMessage());
                }
            }
        }
    }
}