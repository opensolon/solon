package org.noear.solon.core.message;

import org.noear.solon.annotation.Note;

/**
 * 消息标志
 *
 * @author noear
 * @since 1.2
 * */
public interface MessageFlag {
    /**
     * 容器包（承载另一个消息包的字节码；一般用于加密）
     * */
    @Note("容器包（用于二次编码）")
    int container = 1;

    /**
     * 消息包
     * */
    @Note("消息包")
    int message = 10;
    /**
     * 心跳消息包
     * */
    @Note("心跳消息包")
    int heartbeat = 11;
    /**
     * 握手消息包
     * */
    @Note("握手消息包")
    int handshake = 12;
    /**
     * 请求体消息包
     * */
    @Note("请求体消息包")
    int request = 13;
    /**
     * 响应体消息包
     * */
    @Note("响应体消息包")
    int response = 14;
}
