package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.XMethod;
import org.noear.solonx.socket.api.*;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XEventBus;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    private Charset _charset = StandardCharsets.UTF_8;

    private XSocketHandler handler;
    private XSocketListener listening;

    public WsServer(int port) {
        super(new InetSocketAddress(port));
        handler = new XSocketHandler(XMethod.WEBSOCKET);

        Aop.getAsyn(XSocketListener.class, (bw) -> listening = bw.raw());
    }

    @Override
    public void onStart() {
        System.out.println("Solon.Server:Websocket onStart...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
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
        if (listening != null) {
            listening.onClose(_SocketSession.get(conn));
            _SocketSession.remove(conn);
        }
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            XSession session = _SocketSession.get(conn);
            XSocketMessage message = XSocketMessage.wrap(conn.getResourceDescriptor(), data.getBytes(_charset));

            if (listening != null) {
                listening.onMessage(session, message);
            }

            if (message.getHandled() == false) {
                handler.handle(session, message, true);
            }

        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            XSession session = _SocketSession.get(conn);
            XSocketMessage message = XSocketMessage.wrap(conn.getResourceDescriptor(), data.array());

            if (listening != null) {
                listening.onMessage(session, message);
            }

            if (message.getHandled() == false) {
                handler.handle(session, message, false);
            }
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if (listening != null) {
            listening.onError(_SocketSession.get(conn), ex);
        } else {
            XEventBus.push(ex);
        }
    }
}
