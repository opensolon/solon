package org.noear.solonx.socket.api;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * XSocket 会话
 * */
public interface XSession {
    /**
     * 发送消息
     * */
    void send(String message);
    /**
     * 发送消息
     * */
    void send(byte[] message);

    /**
     * 关闭会话
     * */
    void close() throws IOException;

    /**
     * 会话是否有效
     * */
    boolean isValid();

    /**
     * 远程地址
     * */
    InetSocketAddress getRemoteAddress() throws IOException;
    /**
     * 本地地址
     * */
    InetSocketAddress getLocalAddress() throws IOException;

    /**
     * 设置附件
     * */
    void setAttachment(Object obj);
    /**
     * 获取附件
     * */
    <T> T getAttachment();

    /**
     * 获取所有会话
     * */
    Collection<XSession> getOpenSessions();
}
