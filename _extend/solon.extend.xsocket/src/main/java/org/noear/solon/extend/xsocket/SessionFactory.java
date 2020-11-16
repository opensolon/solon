package org.noear.solon.extend.xsocket;

import org.noear.solon.core.message.Session;

public abstract class SessionFactory {
    private static SessionFactory instance;

    public static void setInstance(SessionFactory factory) {
        instance = factory;
    }

    protected abstract Session getSession(Object conn);

    protected abstract Session createSession(String host, int port);


    public static Session get(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.getSession(conn);
    }

    public static Session create(String host, int port) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.createSession(host, port);
    }
}
