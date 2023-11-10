package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.util.LogUtil;
import org.noear.solon.net.websocket.WebSocketRouter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(WsServer.class);
    private WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

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
        webSocketRouter.getListener().onOpen(webSocket);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        _WebSocketImpl webSocket = conn.getAttachment();
        if (webSocket.isClosed()) {
            return;
        } else {
            webSocket.close();
        }
        webSocketRouter.getListener().onClose(webSocket);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            webSocketRouter.getListener().onMessage(conn.getAttachment(), data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            webSocketRouter.getListener().onMessage(conn.getAttachment(), data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            webSocketRouter.getListener().onError(conn.getAttachment(), ex);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
