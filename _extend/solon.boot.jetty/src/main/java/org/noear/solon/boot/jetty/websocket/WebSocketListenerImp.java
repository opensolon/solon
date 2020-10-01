package org.noear.solon.boot.jetty.websocket;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XMethod;
import org.noear.solonx.socket.api.XSocketHandler;
import org.noear.solonx.socket.api.XSocketListener;

public class WebSocketListenerImp extends WebSocketAdapter {
    XSocketHandler handler;
    XSocketListener listener;

    public WebSocketListenerImp() {
        handler = new XSocketHandler(XMethod.WEBSOCKET);
        Aop.getAsyn(XSocketListener.class, (bw) -> handler = bw.raw());
    }

    @Override
    public void onWebSocketConnect(Session sess) {
        super.onWebSocketConnect(sess);
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int len) {
        super.onWebSocketBinary(payload, offset, len);
    }

    @Override
    public void onWebSocketText(String message) {
        super.onWebSocketText(message);

    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
    }
}
