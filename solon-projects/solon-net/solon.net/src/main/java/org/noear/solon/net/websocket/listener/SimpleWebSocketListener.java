package org.noear.solon.net.websocket.listener;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear
 * @since 2.0
 */
public class SimpleWebSocketListener implements WebSocketListener {

    @Override
    public void onOpen(WebSocket socket) {

    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {

    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {

    }

    @Override
    public void onClose(WebSocket socket) {

    }

    @Override
    public void onError(WebSocket socket, Throwable error) {

    }
}
