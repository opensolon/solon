package org.noear.solon.boot.jetty.websocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.net.Socket;
import java.util.Collection;
import java.util.Collections;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof org.eclipse.jetty.websocket.api.Session) {
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
        if (conn instanceof org.eclipse.jetty.websocket.api.Session) {
            _SocketSession.remove((org.eclipse.jetty.websocket.api.Session) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a jetty websocket Session type");
        }
    }
}
