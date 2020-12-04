package org.noear.solon.boot.jdksocket;

import org.noear.solon.Utils;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;
import org.noear.solon.extend.socketd.SessionManager;

import java.net.Socket;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;

class _SessionManagerImpl extends SessionManager {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof Socket) {
            return _SocketSession.get((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }

    @Override
    protected Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketSession.sessions.values());
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof Socket) {
            _SocketSession.remove((Socket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a Socket type");
        }
    }
}
