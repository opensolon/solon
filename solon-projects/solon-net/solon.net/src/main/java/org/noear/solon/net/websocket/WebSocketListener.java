package org.noear.solon.net.websocket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * WebSoskcet 监听器
 *
 * @author noear
 * @since 2.6
 */
public interface WebSocketListener {
    /**
     * 连接打开时（可以做个签权）
     */
    void onOpen(WebSocket socket);

    /**
     * 收到消息时
     */
    void onMessage(WebSocket socket, String text) throws IOException;

    /**
     * 收到消息时
     */
    void onMessage(WebSocket socket, ByteBuffer binary) throws IOException;

    /**
     * 连接关闭时
     */
    void onClose(WebSocket socket);

    /**
     * 出错时
     */
    void onError(WebSocket socket, Throwable error);

    /**
     * Ping 时
     */
    default void onPing(WebSocket socket) {
    }

    /**
     * Pong 时
     */
    default void onPong(WebSocket socket) {
    }
}
