package org.noear.solon.net.websocket;

import org.noear.solon.net.websocket.Listener.PipelineWebSocketListener;

/**
 * @author noear
 * @since 2.0
 */
public class WebSocketBus{
    private static WebSocketListener listener = new PipelineWebSocketListener();

    public static void setListener(WebSocketListener listener) {
        WebSocketBus.listener = listener;
    }

    public static WebSocketListener getListener() {
        return listener;
    }
}
