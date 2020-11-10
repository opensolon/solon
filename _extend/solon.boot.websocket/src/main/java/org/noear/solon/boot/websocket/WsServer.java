package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.*;
import org.noear.solon.extend.xsocket.XListenerProxy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    private Charset _charset = StandardCharsets.UTF_8;

    public WsServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        System.out.println("Solon.Server:Websocket onStart...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        XListenerProxy.getGlobal().onOpen(_SocketSession.get(conn));
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        XListenerProxy.getGlobal().onClose(_SocketSession.get(conn));

        _SocketSession.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            XSession session = _SocketSession.get(conn);
            XMessage message = XMessage.wrap(conn.getResourceDescriptor(), data.getBytes(_charset));

            XListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            XSession session = _SocketSession.get(conn);
            XMessage message = XMessage.wrap(conn.getResourceDescriptor(), data.array());

            XListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            XEventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        XListenerProxy.getGlobal().onError(_SocketSession.get(conn), ex);
    }
}
