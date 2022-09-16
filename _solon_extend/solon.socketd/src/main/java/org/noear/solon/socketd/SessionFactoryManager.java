package org.noear.solon.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 会话工厂管理者
 *
 * @author noear
 * @since 1.2
 * */
public class SessionFactoryManager {
    public static Map<String, SessionFactory> uriCached = new HashMap<>();
    public static Map<Class<?>, SessionFactory> clzCached = new HashMap<>();

    /**
     * 注册会话工厂
     * */
    public static void register(SessionFactory factory) {
        for (String p : factory.schemes()) {
            uriCached.putIfAbsent(p, factory);
        }

        clzCached.putIfAbsent(factory.driveType(), factory);
    }

    /**
     * 创建会话
     *
     * @param connector 链接器
     * */
    public static Session create(Connector connector) {
        SessionFactory factory = clzCached.get(connector.driveType());

        if (factory == null) {
            throw new IllegalArgumentException("The connector is not supported");
        }

        Session session = factory.createSession(connector);
        session.setHandshaked(true);

        return session;
    }

    /**
     * 创建会话
     *
     * @param uri 链接地址
     * @param autoReconnect 是否自动链接
     * */
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
