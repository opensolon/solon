package org.noear.solon.extend.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class SessionFactoryManager {
    public static Map<String, SessionFactory> cached = new HashMap<>();

    public static void register(SessionFactory factory) {
        for (String p : factory.schemes()) {
            cached.putIfAbsent(p, factory);
        }
    }

    public static Session create(URI uri, boolean autoReconnect) {
        SessionFactory factory = cached.get(uri.getScheme());

        Session session = factory.createSession(uri, autoReconnect);
        session.setHandshaked(true);

        return session;
    }
}
