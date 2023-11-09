package org.noear.solon.net.websocket;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * @author noear
 * @since 2.6
 */
public interface WebSocket extends Closeable {
    /**
     * 是否有效
     */
    boolean isValid();

    /**
     * 是否安全
     * */
    boolean isSecure();

    /**
     * 获取握手信息
     */
    Handshake getHandshake();

    /**
     * 获取远程地址
     */
    InetSocketAddress getRemoteAddress() throws IOException;

    /**
     * 获取本地地址
     */
    InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 设置连接附件
     */
    <T> void setAttachment(T attachment);

    /**
     * 获取连接附件
     */
    <T> T getAttachment();

    /**
     * 获取所有属性
     */
    Map<String, Object> getAttrMap();

    /**
     * 获取属性
     *
     * @param name 名字
     */
    <T> T getAttr(String name);

    /**
     * 获取属性或默认值
     *
     * @param name 名字
     * @param def  默认值
     */
    <T> T getAttrOrDefault(String name, T def);

    /**
     * 设置属性
     *
     * @param name  名字
     * @param value 值
     */
    <T> void setAttr(String name, T value);

    /**
     *
     */
    void send(String text);

    /**
     *
     */
    void send(ByteBuffer binary);
}