package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.util.Collection;

public abstract class SessionFactory {
    private static SessionFactory instance;

    public static void setInstance(SessionFactory factory) {
        instance = factory;
    }

    protected abstract Session getSession(Object conn);
    protected abstract Collection<Session> getOpenSessions();

    protected abstract void removeSession(Object conn);

    protected abstract Session createSession(String host, int port, boolean autoReconnect);




    //
    // for server
    //
    public static Session get(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("SessionFactory uninitialized");
        }

        return instance.getSession(conn);
    }

    public static Collection<Session> getOpens(){
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

    //
    // for client
    //

    public static Session create(String host, int port, boolean autoReconnect) {
        if (instance == null) {
            throw new IllegalArgumentException("SessionFactory uninitialized");
        }

        return instance.createSession(host, port, autoReconnect);
    }
}
