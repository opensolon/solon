package org.noear.solon.boot.socketd.jdksocket;

import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SessionManager;
import org.noear.solon.socketd.client.jdksocket.BioSocketSession;

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
            return BioSocketSession.get((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(BioSocketSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof Socket) {
            BioSocketSession.remove((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }
}
