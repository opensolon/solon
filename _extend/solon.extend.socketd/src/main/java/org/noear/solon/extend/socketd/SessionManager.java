package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.util.Collection;

/**
 * 为服务端提供统一的管理接口
 *
 * @author noear
 * @since 1.2
 * */
public abstract class SessionManager {
    private static SessionManager instance;

    public static void setInstance(SessionManager factory) {
        instance = factory;
    }

    public static void setInstanceIfAbsent(SessionManager factory) {
        if (instance == null) {
            instance = factory;
        }
    }

    protected abstract Session getSession(Object conn);

    protected abstract Collection<Session> getOpenSessions();

    protected abstract void removeSession(Object conn);


    //
    // for server
    //
    public static Session get(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("SessionFactory uninitialized");
        }

        return instance.getSession(conn);
    }

    public static Collection<Session> getOpens() {
        if (instance == null) {
            throw new IllegalArgumentException("SessionFactory uninitialized");
        }

        return instance.getOpenSessions();
    }


    public static void remove(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("SessionFactory uninitialized");
        }

        instance.removeSession(conn);
    }
}
