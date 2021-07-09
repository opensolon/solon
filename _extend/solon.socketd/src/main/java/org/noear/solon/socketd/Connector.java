package org.noear.solon.socketd;

import org.noear.solon.core.message.Session;

import java.io.IOException;
import java.net.URI;

/**
 * SocketD 链接器
 *
 * @author noear
 * @since 1.2
 * */
public interface Connector<T> {
    /**
     * 连接地址
     * */
    URI uri();

    /**
     * 是否自动连接
     * */
    boolean autoReconnect();

    /**
     * 驱动类型
     * */
    Class<T> driveType();

    /**
     * 打开
     *
     * @param session SocketD 会话
     * */
    T open(Session session) throws IOException;
}
