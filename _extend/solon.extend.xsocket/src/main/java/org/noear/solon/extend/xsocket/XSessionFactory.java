package org.noear.solon.extend.xsocket;

import org.noear.solon.core.message.MessageSession;

public abstract class XSessionFactory {
    private static XSessionFactory instance;

    public static void setInstance(XSessionFactory factory) {
        instance = factory;
    }

    protected abstract MessageSession getSession(Object conn);

    protected abstract MessageSession createSession(String host, int port);


    public static MessageSession get(Object conn) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.getSession(conn);
    }

    public static MessageSession create(String host, int port) {
        if (instance == null) {
            throw new IllegalArgumentException("XSessionFactory uninitialized");
        }

        return instance.createSession(host, port);
    }
}
