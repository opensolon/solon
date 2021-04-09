package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;
import org.smartboot.http.server.WebSocketRequest;

import java.util.Collection;
import java.util.Collections;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.WEBSOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof WebSocketRequest) {
            return _SocketServerSession.getOnly((WebSocketRequest) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a smarthttp WebSocketRequest type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof WebSocketRequest) {
            _SocketServerSession.remove((WebSocketRequest) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a smarthttp WebSocketRequest type");
        }
    }
}
