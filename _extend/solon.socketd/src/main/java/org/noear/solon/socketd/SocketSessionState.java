package org.noear.solon.socketd;

import org.noear.solon.core.handle.SessionState;
import org.noear.solon.core.message.Session;

/**
 * @author noear
 * @since 1.6
 */
public class SocketSessionState implements SessionState {
    Session session;
    public SocketSessionState(Session session){
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
    public Object sessionGet(String key) {
        return session.attr(key);
    }

    @Override
    public void sessionSet(String key, Object val) {
        if (val == null) {
            sessionRemove(key);
        } else {
            session.attrSet(key, val);
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
