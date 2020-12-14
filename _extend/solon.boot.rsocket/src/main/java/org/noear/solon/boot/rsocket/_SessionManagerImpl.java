package org.noear.solon.boot.rsocket;

import io.rsocket.RSocket;
import org.noear.solon.core.SignalType;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.util.Collection;
import java.util.Collections;

/**
 * @author noear 2020/12/14 created
 * @since 1.2
 */
public class _SessionManagerImpl extends SessionManager {
    @Override
    protected SignalType signalType() {
        return SignalType.SOCKET;
    }

    @Override
    public Session getSession(Object conn) {
        if (conn instanceof RSocket) {
            return _SocketSession.get((RSocket) conn);
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
        if (conn instanceof RSocket) {
            _SocketSession.remove((RSocket) conn);
        } else {
            throw new IllegalArgumentException("This conn requires a netty Channel type");
        }
    }
}
