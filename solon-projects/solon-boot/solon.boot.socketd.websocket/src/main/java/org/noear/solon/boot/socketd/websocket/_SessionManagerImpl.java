package org.noear.solon.boot.socketd.websocket;

import org.java_websocket.WebSocket;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;

import java.util.Collection;
import java.util.Collections;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.WEBSOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof WebSocket) {
            return _SocketServerSession.get((WebSocket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a java_websocket WebSocket type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof WebSocket) {
            _SocketServerSession.remove((WebSocket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a java_websocket WebSocket type");
        }
    }
}
