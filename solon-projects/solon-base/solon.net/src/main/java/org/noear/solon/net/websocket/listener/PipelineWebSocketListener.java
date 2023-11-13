package org.noear.solon.net.websocket.listener;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * @author noear
 * @since 2.0
 */
public class PipelineWebSocketListener implements WebSocketListener {
    protected final Deque<WebSocketListener> pipeline = new LinkedList<>();
    protected final Set<Class<?>> typeSet = new HashSet<>();

    /**
     * 前一个
     */
    public PipelineWebSocketListener prev(WebSocketListener listener) {
        pipeline.addFirst(listener);
        return this;
    }

    /**
     * 前一个
     */
    public PipelineWebSocketListener prevIfAbsent(WebSocketListener listener) {
        if (typeSet.contains(listener.getClass()) == false) {
            typeSet.add(listener.getClass());
            pipeline.addFirst(listener);
        }


        return this;
    }

    /**
     * 后一个
     */
    public PipelineWebSocketListener next(WebSocketListener listener) {
        pipeline.addLast(listener);
        return this;
    }

    /**
     * 后一个
     */
    public PipelineWebSocketListener nextIfAbsent(WebSocketListener listener) {
        if (typeSet.contains(listener.getClass()) == false) {
            typeSet.add(listener.getClass());
            pipeline.addLast(listener);
        }

        return this;
    }

    @Override
    public void onOpen(WebSocket socket) {
        for (WebSocketListener listener : pipeline) {
            listener.onOpen(socket);
        }
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        for (WebSocketListener listener : pipeline) {
            listener.onMessage(socket, text);
        }
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        for (WebSocketListener listener : pipeline) {
            listener.onMessage(socket, binary);
        }
    }

    @Override
    public void onClose(WebSocket socket) {
        for (WebSocketListener listener : pipeline) {
            listener.onClose(socket);
        }
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        for (WebSocketListener listener : pipeline) {
            listener.onError(socket, error);
        }
    }
}
