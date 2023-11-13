package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class WebSocketListenerImpl extends WebSocketAdapter {
    static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);

    private WebSocketImpl webSocket;
    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    @Override
    public void onWebSocketConnect(org.eclipse.jetty.websocket.api.Session sess) {
        super.onWebSocketConnect(sess);

        webSocket = new WebSocketImpl(sess);

        sess.getUpgradeRequest().getHeaders().forEach((k, v) -> {
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
            webSocket.close();
        }

        webSocketRouter.getListener().onClose(webSocket);
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        webSocketRouter.getListener().onError(webSocket, cause);
    }
}
