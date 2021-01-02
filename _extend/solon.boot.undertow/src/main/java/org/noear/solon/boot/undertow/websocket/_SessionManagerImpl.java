package org.noear.solon.boot.undertow.websocket;

import io.undertow.websockets.core.WebSocketChannel;
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
        if (conn instanceof WebSocketChannel) {
            return _SocketServerSession.get((WebSocketChannel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
        }
    }

    @Override
    public Collection<Session> getOpenSessions() {
        return Collections.unmodifiableCollection(_SocketServerSession.sessions.values());
    }

    @Override
    public void removeSession(Object conn) {
        if (conn instanceof WebSocketChannel) {
            _SocketServerSession.remove((WebSocketChannel) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a undertow WebSocketChannel type");
        }
    }
}
