package org.noear.solon.boot.socketd.smartsocket;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;
import org.smartboot.socket.transport.AioSession;

import java.util.Collection;
import java.util.Collections;

class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.SOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof AioSession) {
            return _SocketSession.get((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a smartsocket AioSession type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof AioSession) {
            _SocketSession.remove((AioSession) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a smartsocket AioSession type");
        }
    }
}