package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.noear.solon.net.websocket.WebSocketBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public class WebSocketListenerImpl extends WebSocketAdapter {
    static final Logger log = LoggerFactory.getLogger(WebSocketListenerImpl.class);

    private  _WebSokcetImpl webSokcet;

    @Override
    public void onWebSocketConnect(org.eclipse.jetty.websocket.api.Session sess) {
        super.onWebSocketConnect(sess);

        webSokcet = new _WebSokcetImpl(sess);

        sess.getUpgradeRequest().getHeaders().forEach((k, v) -> {
            if (v.size() > 0) {
                webSokcet.getHandshake().putParam(k, v.get(0));
            }
        });

        WebSocketBus.getListener().onOpen(webSokcet);
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        try {
            ByteBuffer buf = ByteBuffer.wrap(payload, offset, len);
            WebSocketBus.getListener().onMessage(webSokcet, buf);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onWebSocketText(String text) {
        try {
            WebSocketBus.getListener().onMessage(webSokcet, text);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        WebSocketBus.getListener().onClose(webSokcet);
        super.onWebSocketClose(statusCode, reason);
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        WebSocketBus.getListener().onError(webSokcet, cause);
    }
}
