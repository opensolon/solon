package org.noear.solon.boot.jetty.websocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;

import java.net.Socket;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

public class _SessionFactoryImpl extends SessionFactory {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof Socket) {
            return _SocketSession.get((org.eclipse.jetty.websocket.api.Session) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a jetty websocket Session type");
        }
    }

    @Override
    protected Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketSession.sessions.values());
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof Socket) {
            _SocketSession.remove((org.eclipse.jetty.websocket.api.Session) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a jetty websocket Session type");
        }
    }

    @Override
    protected Session createSession(URI uri, boolean autoReconnect) {
        try {
            WsConnector bioClient = new WsConnector(uri.getHost(), uri.getPort());

            return new _SocketSession(bioClient, autoReconnect);
        } catch (Exception ex) {
            throw Utils.throwableWrap(ex);
        }
    }
}
