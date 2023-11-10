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
    void onOpen(WebSocket socket);

    void onMessage(WebSocket socket, String text) throws IOException;

    void onMessage(WebSocket socket, ByteBuffer binary) throws IOException;

    void onClose(WebSocket socket);

    void onError(WebSocket socket, Throwable error);
}
