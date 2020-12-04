package org.noear.solon.extend.socketd;

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

    Session createSession(URI uri, boolean autoReconnect);
}
