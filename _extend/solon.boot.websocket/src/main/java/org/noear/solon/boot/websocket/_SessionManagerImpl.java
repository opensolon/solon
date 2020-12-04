package org.noear.solon.boot.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.util.Collection;
import java.util.Collections;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof WebSocket) {
            return _SocketServerSession.get((WebSocket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a java_websocket WebSocket type");
        }
    }

    @Override
    protected Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof WebSocket) {
            _SocketServerSession.remove((WebSocket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a java_websocket WebSocket type");
        }
    }
}
