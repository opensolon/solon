package org.noear.solon.boot.socketd.netty;

import io.netty.channel.Channel;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;

import java.util.Collection;
import java.util.Collections;

class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.SOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof Channel) {
            return _SocketSession.get((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a netty Channel type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof Channel) {
            _SocketSession.remove((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a netty Channel type");
        }
    }
}
