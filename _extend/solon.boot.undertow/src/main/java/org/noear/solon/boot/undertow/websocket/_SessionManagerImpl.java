package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.util.Collection;
import java.util.Collections;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected Session getSession(Object conn) {
        if (conn instanceof WebSocketChannel) {
            return _SocketServerSession.get((WebSocketChannel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
        }
    }

    @Override
    protected Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
    }

    @Override
    protected void removeSession(Object conn) {
        if (conn instanceof WebSocketChannel) {
            _SocketServerSession.remove((WebSocketChannel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
        }
    }
}
