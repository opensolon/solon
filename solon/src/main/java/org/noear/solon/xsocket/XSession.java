package org.noear.solon.xsocket;

import org.noear.solon.core.XMethod;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;

/**
 * XSocket 会话
 *
 * @author noear
 * @since 1.0
 * */
public interface XSession {
    Object real();

    /**
     * 协议
     * */
    XMethod method();
    /**
     * 资源路径（socket 为空）
     * */
    String path();

    /**
     * 发送消息
     * */
    void send(String message);
    /**
     * 发送消息
     * */
    void send(byte[] message);
    /**
     * 发送消息
     * */
    void send(XMessage message);

    /**
     * 关闭会话
     * */
    void close() throws IOException;

    /**
     * 是否是有效的
     * */
    boolean isValid();
    /**
     * 是否是安全的
     * */
    boolean isSecure();

    /**
     * 远程地址
     * */
    InetSocketAddress getRemoteAddress();
    /**
     * 本地地址
     * */
    InetSocketAddress getLocalAddress();

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
