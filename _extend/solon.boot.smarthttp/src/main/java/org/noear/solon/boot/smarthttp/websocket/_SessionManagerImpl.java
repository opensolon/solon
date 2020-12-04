package org.noear.solon.boot.smarthttp.websocket;

import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionManager;

import java.util.Collection;

public class _SessionManagerImpl extends SessionManager {
    @Override
    protected Session getSession(Object conn) {
        return null;
    }

    @Override
    protected Collection<Session> getOpenSessions() {
        return null;
    }

    @Override
    protected void removeSession(Object conn) {

    }
}
