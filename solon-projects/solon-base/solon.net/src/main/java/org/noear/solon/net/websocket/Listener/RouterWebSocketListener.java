package org.noear.solon.net.websocket.Listener;

import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 2.5
 */
public class RouterWebSocketListener implements WebSocketListener{
    protected final Map<String, WebSocketListener> routingTable = new HashMap<>();



    /**
     * 匹配
     */
    protected WebSocketListener matching(WebSocket socket) {
        return routingTable.get(socket.getHandshake().getPath());
    }


    /**
     * 路由
     */
    public RouterWebSocketListener of(String path, WebSocketListener listener) {
        routingTable.put(path, listener);
        return this;
    }

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
