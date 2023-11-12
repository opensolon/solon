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
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    static final Logger log = LoggerFactory.getLogger(WsServer.class);

    private final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

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


    private WebSocketImpl getSession(WebSocket conn) {
        return getSession(conn, null);
    }

    private WebSocketImpl getSession(WebSocket conn, ClientHandshake shake) {
        WebSocketImpl session = conn.getAttachment();

        if (session == null) {
            //直接从附件拿，不一定可靠
            session = new WebSocketImpl(conn);
            conn.setAttachment(session);

            if (shake != null) {
                Iterator<String> httpFields = shake.iterateHttpFields();
                while (httpFields.hasNext()) {
                    String name = httpFields.next();
                    session.putParam(name, shake.getFieldValue(name));
                }
            }
        }

        return session;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        WebSocketImpl webSocket = getSession(conn, shake);
        webSocketRouter.getListener().onOpen(webSocket);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        WebSocketImpl webSocket = getSession(conn);
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
            WebSocketImpl webSocket = getSession(conn);
            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            WebSocketImpl webSocket = getSession(conn);
            webSocketRouter.getListener().onMessage(webSocket, data);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        try {
            WebSocketImpl webSocket = getSession(conn);
            webSocketRouter.getListener().onError(webSocket, ex);
        } catch (Throwable e) {
            log.warn(e.getMessage(), e);
        }
    }
}
