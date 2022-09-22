package org.noear.solon.boot.socketd.websocket;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.noear.solon.Solon;
import org.noear.solon.core.event.EventBus;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.core.util.PrintUtil;
import org.noear.solon.socketd.ProtocolManager;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@SuppressWarnings("unchecked")
public class WsServer extends WebSocketServer {
    public WsServer(int port) {
        super(new InetSocketAddress(port));
    }

    public WsServer(InetAddress addr, int port) {
        super(new InetSocketAddress(addr,port));
    }

    @Override
    public void onStart() {
        PrintUtil.info("Server:Websocket onStart...");
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake shake) {
        if(conn == null){
            return;
        }
        Session session = _SocketServerSession.get(conn);
        shake.iterateHttpFields().forEachRemaining(k -> {
            session.headerSet(k, shake.getFieldValue(k));
        });

        Solon.app().listener().onOpen(session);
    }

    @Override
    public void onClose(WebSocket conn, int i, String s, boolean b) {
        if(conn == null){
            return;
        }

        Solon.app().listener().onClose(_SocketServerSession.get(conn));

        _SocketServerSession.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String data) {

    }

    @Override
    public void onMessage(WebSocket conn, ByteBuffer data) {
        if(conn == null){
            return;
        }

        try {
            Session session = _SocketServerSession.get(conn);
            Message message = ProtocolManager.decode(data);

            Solon.app().listener().onMessage(session, message);
        } catch (Throwable ex) {
            EventBus.push(ex);
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        if(conn == null){
            return;
        }

        Solon.app().listener().onError(_SocketServerSession.get(conn), ex);
    }
}
