package org.noear.solon.net.stomp;

import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Stomp 会话
 *
 * @author noear
 * @since 3.0
 */
public interface StompSession {
    WebSocket getSocket();

    /**
     * id
     */
    String id();

    /**
     * 名字
     */
    String name();

    /**
     * 名字命名为
     */
    void nameAs(String name);

    /**
     * 获取参数
     *
     * @param name 参数名
     */
    String param(String name);

    /**
     * 获取参数或默认值
     *
     * @param name 参数名
     * @param def  默认值
     */
    String paramOrDefault(String name, String def);

    /**
     * 获取远程地址
     */
    InetSocketAddress remoteAddress() throws IOException;

    /**
     * 发送
     */
    void send(Frame frame);

    /**
     * 关闭会话
     */
    void close();
}
