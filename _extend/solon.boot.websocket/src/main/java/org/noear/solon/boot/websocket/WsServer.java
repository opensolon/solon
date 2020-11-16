package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.MessageListenerProxy;

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
        MessageListenerProxy.getGlobal().onOpen(_SocketSession.get(conn));
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        MessageListenerProxy.getGlobal().onClose(_SocketSession.get(conn));

        _SocketSession.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {
        try {
            Session session = _SocketSession.get(conn);
            Message message = Message.wrap(conn.getResourceDescriptor(), data.getBytes(_charset));

            MessageListenerProxy.getGlobal().onMessage(session, message, true);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        try {
            Session session = _SocketSession.get(conn);
            Message message = Message.wrap(conn.getResourceDescriptor(), data.array());

            MessageListenerProxy.getGlobal().onMessage(session, message, false);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        MessageListenerProxy.getGlobal().onError(_SocketSession.get(conn), ex);
    }
}
