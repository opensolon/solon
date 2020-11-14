package org.noear.solon.extend.xsocket;

import org.noear.solon.core.XSession;

public abstract class XSessionFactory {
    private static XSessionFactory instance;

    public static void setInstance(XSessionFactory factory) {
        instance = factory;
    }

    protected abstract XSession getSession(Object conn);

    protected abstract XSession createSession(String host, int port);


    public static XSession get(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.getSession(conn);
    }

    public static XSession create(String host, int port) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.createSession(host, port);
    }
}
