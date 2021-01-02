package org.noear.solon.boot.socketd.netty;

import io.netty.channel.Channel;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.client.netty.NioSocketSession;

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
            return NioSocketSession.get((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a netty Channel type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(NioSocketSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof Channel) {
            NioSocketSession.remove((Channel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a netty Channel type");
        }
    }
}
