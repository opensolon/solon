package org.noear.solon.extend.socketd.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.URI;

public class _SessionFactoryImpl implements SessionFactory {
    @Override
    public String[] schemes() {
        return new String[]{"ws"};
    }

    @Override
    public Session createSession(URI uri, boolean autoReconnect) {
        try {
            WsConnector bioClient = new WsConnector(uri);

            return new _SocketClientSession(bioClient, autoReconnect);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
