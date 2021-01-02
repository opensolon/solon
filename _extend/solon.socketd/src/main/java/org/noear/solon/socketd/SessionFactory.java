package org.noear.solon.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;

/**
 * 为客户端提供统一的会话创建界面
 *
 * @author noear
 * @since 1.2
 * */
public interface SessionFactory {
    String[] schemes();

    Class<?> driveType();

    Session createSession(Connector connector);

    Session createSession(URI uri, boolean autoReconnect);
}
