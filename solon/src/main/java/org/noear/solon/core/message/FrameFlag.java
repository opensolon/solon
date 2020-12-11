package org.noear.solon.core.message;

/**
 * 帧标志
 *
 * @author noear
 * @since 1.2
 * */
public interface FrameFlag {
    /**
     * 容器包（用于加密承载）
     * */
    int container = 1;

    /**
     * 消息包
     * */
    int message = 10;
    /**
     * 心跳消息包
     * */
    int heartbeat = 11;
    /**
     * 握手消息包
     * */
    int handshake = 12;
    /**
     * 响应消息包
     * */
    int response = 13;
}
