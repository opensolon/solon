package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.net.Socket;
import java.util.Collection;
import java.util.Collections;

class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.SOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof Socket) {
            return _SocketSession.get((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof Socket) {
            _SocketSession.remove((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }
}
