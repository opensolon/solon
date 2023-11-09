package org.noear.solon.net.websocket.Listener;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.LinkedList;

/**
 * @author noear
 * @since 2.0
 */
public class PipelineWebSocketListener implements WebSocketListener {
    protected final Deque<WebSocketListener> deque = new LinkedList<>();

    /**
     * 前一个
     */
    public PipelineWebSocketListener prev(WebSocketListener listener) {
        deque.addFirst(listener);
        return this;
    }

    /**
     * 后一个
     */
    public PipelineWebSocketListener next(WebSocketListener listener) {
        deque.addLast(listener);
        return this;
    }

    @Override
    public void onOpen(WebSocket socket) {
        for (WebSocketListener listener : deque) {
            listener.onOpen(socket);
        }
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        for (WebSocketListener listener : deque) {
            listener.onMessage(socket, text);
        }
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        for (WebSocketListener listener : deque) {
            listener.onMessage(socket, binary);
        }
    }

    @Override
    public void onClose(WebSocket socket) {
        for (WebSocketListener listener : deque) {
            listener.onClose(socket);
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        for (WebSocketListener listener : deque) {
            listener.onError(socket, error);
        }
    }
}
