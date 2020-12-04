package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.ListenerProxy;
import org.noear.solon.extend.socketd.MessageWrapper;

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
        ListenerProxy.getGlobal().onOpen(_SocketServerSession.get(conn));
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        ListenerProxy.getGlobal().onClose(_SocketServerSession.get(conn));

        _SocketServerSession.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            Session session = _SocketServerSession.get(conn);
            Message message = MessageWrapper.wrap(conn.getResourceDescriptor(), null,data.getBytes(_charset));

            ListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            Session session = _SocketServerSession.get(conn);
            Message message = MessageWrapper.wrap(conn.getResourceDescriptor(), null,data.array());

            ListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        ListenerProxy.getGlobal().onError(_SocketServerSession.get(conn), ex);
    }
}
