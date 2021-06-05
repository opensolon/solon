package org.noear.solon.socketd.client.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.Connector;
import org.noear.solon.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"ws", "wss"};
    }

    @Override
    public Class<?> driveType() {
        return WebSocket.class;
    }

    @Override
    public Session createSession(Connector connector) {
        if (connector.driveType() == WebSocket.class) {
            return new _SocketClientSession((Connector<WebSocket>) connector);
        } else {
            throw new IllegalArgumentException("Only support Connector<WebSocket> connector");
        }
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        WsConnector bioClient = new WsConnector(uri, autoReconnect);
        return new _SocketClientSession(bioClient);
    }
}
