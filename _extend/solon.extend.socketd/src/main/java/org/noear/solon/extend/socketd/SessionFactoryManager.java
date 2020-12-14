package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SessionFactoryManager {
    public static Map<String, SessionFactory> uriCached = new HashMap<>();
    public static Map<Class<?>, SessionFactory> clzCached = new HashMap<>();

    public static void register(SessionFactory factory) {
        for (String p : factory.schemes()) {
            uriCached.putIfAbsent(p, factory);
        }

        clzCached.putIfAbsent(factory.driveType(), factory);
    }

    public static Session create(Connector connector) {
        SessionFactory factory = clzCached.get(connector.driveType());

        if (factory == null) {
            throw new IllegalArgumentException("The connector is not supported");
        }

        Session session = factory.createSession(connector);
        session.setHandshaked(true);

        return session;
    }

    public static Session create(URI uri, boolean autoReconnect) {
        SessionFactory factory = uriCached.get(uri.getScheme());

        if (factory == null) {
            throw new IllegalArgumentException("The " + uri.getScheme() + " protocol is not supported");
        }

        Session session = factory.createSession(uri, autoReconnect);
        session.setHandshaked(true);

        return session;
    }
}
