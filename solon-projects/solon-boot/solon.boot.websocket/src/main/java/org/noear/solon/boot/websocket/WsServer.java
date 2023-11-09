package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.net.websocket.WebSocketBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(WsServer.class);

    public WsServer(int port) {
        super(new InetSocketAddress(port));
    }

    public WsServer(InetAddress address, int port) {
        super(new InetSocketAddress(address, port));
    }

    @Override
    public void onStart() {
        LogUtil.global().info("Server:Websocket onStart...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        _WebSocketImpl webSocket = new _WebSocketImpl(conn);

        shake.iterateHttpFields().forEachRemaining(k -> {
            webSocket.getHandshake().putParam(k, shake.getFieldValue(k));
        });

        conn.setAttachment(webSocket);
        WebSocketBus.getListener().onOpen(webSocket);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        WebSocketBus.getListener().onClose(conn.getAttachment());
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            WebSocketBus.getListener().onMessage(conn.getAttachment(), data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            WebSocketBus.getListener().onMessage(conn.getAttachment(), data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            WebSocketBus.getListener().onError(conn.getAttachment(), ex);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
