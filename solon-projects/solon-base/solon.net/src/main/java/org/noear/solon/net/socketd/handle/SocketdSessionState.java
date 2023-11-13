package org.noear.solon.net.socketd.handle;

import org.noear.socketd.transport.core.Session;
import org.noear.solon.core.handle.SessionState;

import java.util.Collection;

/**
 * Socket.D 会话状态适配
 *
 * @author noear
 * @since 2.0
 */
public class SocketdSessionState implements SessionState {
    Session session;

    public SocketdSessionState(Session session) {
        this.session = session;
    }

    @Override
    public boolean replaceable() {
        return false;
    }

    @Override
    public String sessionId() {
        return session.sessionId();
    }

    @Override
    public String sessionChangeId() {
        return session.sessionId();
    }

    @Override
    public Collection<String> sessionKeys() {
        return session.attrMap().keySet();
    }

    @Override
    public <T> T sessionGet(String key, Class<T> clz) {
        return (T) session.attr(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            session.attr(key, val);
        }
    }

    @Override
    public void sessionRemove(String key) {
        session.attrMap().remove(key);
    }

    @Override
    public void sessionClear() {
        session.attrMap().clear();
    }

    @Override
    public void sessionReset() {
        //no sup reset
    }
}
