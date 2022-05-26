package org.noear.solon.socketd;

import org.noear.solon.core.message.Session;

import java.net.URI;

/**
 * 会话工厂。为客户端提供统一的会话创建界面
 *
 * @author noear
 * @since 1.2
 * */
public interface SessionFactory {
    /**
     * 支持的地址架构
     * */
    String[] schemes();

    /**
     * 驱动类型
     * */
    Class<?> driveType();

    /**
     * 创建会话
     *
     * @param connector 链接器
     * */
    Session createSession(Connector connector);

    /**
     * 创建会话
     *
     * @param uri 链接地址
     * @param autoReconnect 是否自动重连
     * */
    Session createSession(URI uri, boolean autoReconnect);
}
