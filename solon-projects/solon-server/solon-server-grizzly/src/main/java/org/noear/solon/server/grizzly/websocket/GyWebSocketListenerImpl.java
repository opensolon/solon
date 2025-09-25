package org.noear.solon.server.grizzly.websocket;

import org.glassfish.grizzly.websockets.DataFrame;
import org.glassfish.grizzly.websockets.WebSocket;
import org.glassfish.grizzly.websockets.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketRouter;

import java.nio.ByteBuffer;

/**
 *
 * @author noear 2025/9/25 created
 *
 */
public class GyWebSocketListenerImpl implements WebSocketListener {
    private static final WebSocketRouter webSocketRouter = WebSocketRouter.getInstance();

    public void onConnect(WebSocket socket) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;

        gyWebSocket.attachment = new WebSocketImpl(gyWebSocket);
        webSocketRouter.getListener().onOpen(gyWebSocket.attachment);
    }

    public void onMessage(WebSocket socket, String text) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        try {
            webSocketRouter.getListener().onMessage(gyWebSocket.attachment, text);
        } catch (Exception ex) {
            webSocketRouter.getListener().onError(gyWebSocket.attachment, ex);
        }
    }

    public void onMessage(WebSocket socket, byte[] bytes) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        try {
            webSocketRouter.getListener().onMessage(gyWebSocket.attachment, ByteBuffer.wrap(bytes));
        } catch (Exception ex) {
            webSocketRouter.getListener().onError(gyWebSocket.attachment, ex);
        }
    }

    public void onPing(WebSocket socket, byte[] bytes) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        webSocketRouter.getListener().onPing(gyWebSocket.attachment);
    }

    @Override
    public void onPong(WebSocket socket, byte[] bytes) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        webSocketRouter.getListener().onPong(gyWebSocket.attachment);
    }

    @Override
    public void onClose(WebSocket socket, DataFrame frame) {
        GyWebSocket gyWebSocket = (GyWebSocket) socket;
        webSocketRouter.getListener().onClose(gyWebSocket.attachment);
    }

    @Override
    public void onFragment(WebSocket webSocket, String s, boolean b) {

    }

    @Override
    public void onFragment(WebSocket webSocket, byte[] bytes, boolean b) {

    }
}
