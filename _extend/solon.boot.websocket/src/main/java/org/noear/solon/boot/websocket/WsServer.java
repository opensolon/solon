package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.api.socket.SocketListening;
import org.noear.solon.api.socket.SocketMessage;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    private WsContextHandler _contextHandler;
    private Charset _charset = StandardCharsets.UTF_8;
    private SocketListening listening;

    public WsServer(int port, WsContextHandler contextHandler) throws UnknownHostException {
        super(new InetSocketAddress(port));
        _contextHandler = contextHandler;

        Aop.getAsyn(SocketListening.class, (bw) -> listening = bw.raw());
    }

    @Override
    public void onStart() {
        System.out.println("Solon.Server:Websocket onStart...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        //System.out.println("Solon.Server:Websocket onOpen=" + shake.getResourceDescriptor());

        if (listening != null) {
            listening.onOpen(_SocketSession.get(conn));
        }
    }

    @Override
    public void onClosing(WebSocket conn, int code, String reason, boolean remote) {
        if (listening != null) {
            listening.onClosing(_SocketSession.get(conn));
            _SocketSession.remove(conn);
        }
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        //System.out.println("Solon.Server:Websocket onClose...");
        if (listening != null) {
            listening.onClose(_SocketSession.get(conn));
            _SocketSession.remove(conn);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            if (listening != null) {
                listening.onMessage(_SocketSession.get(conn), SocketMessage.wrap(conn.getResourceDescriptor(), data.getBytes(_charset)));
            }

            _contextHandler.handle(conn, data.getBytes(_charset), true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            if (listening != null) {
                listening.onMessage(_SocketSession.get(conn), SocketMessage.wrap(conn.getResourceDescriptor(), data.array()));
            }

            _contextHandler.handle(conn, data.array(), false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (listening != null) {
            listening.onError(_SocketSession.get(conn), ex);
        } else {
            XEventBus.push(ex);
        }

        //System.out.println("Solon.Server:Websocket onError:");
        //ex.printStackTrace();
    }
}
