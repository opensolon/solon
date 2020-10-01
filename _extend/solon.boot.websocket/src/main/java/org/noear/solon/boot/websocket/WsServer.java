package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solonx.socket.api.XSocketListener;
import org.noear.solonx.socket.api.XSocketMessage;
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
    private XSocketListener listening;

    public WsServer(int port) {
        super(new InetSocketAddress(port));
        _contextHandler = new WsContextHandler();

        Aop.getAsyn(XSocketListener.class, (bw) -> listening = bw.raw());
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
            XSocketMessage message = XSocketMessage.wrap(conn.getResourceDescriptor(), data.getBytes(_charset));

            if (listening != null) {
                listening.onMessage(_SocketSession.get(conn), message);
            }else {
                _contextHandler.handle(conn, message, true);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            XSocketMessage message = XSocketMessage.wrap(conn.getResourceDescriptor(), data.array());

            if (listening != null) {
                listening.onMessage(_SocketSession.get(conn), message);
            }else {
                _contextHandler.handle(conn, message, false);
            }
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
