package org.noear.solon.net.stomp;

import org.noear.solon.Utils;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;

/**
 * websocket 转 stomp 适配器 (使用 ToStompWebSocketAdapter 比 ToStompWebSocketListener，扩展类更清爽些)
 *
 * @author noear 2024/10/4 created
 */
public class ToStompWebSocketAdapter implements WebSocketListenerSupplier {
    protected ToStompWebSocketListener toStompWebSocketListener;

    public ToStompWebSocketAdapter() {
        ServerEndpoint serverEndpoint = getClass().getAnnotation(ServerEndpoint.class);
        if (serverEndpoint == null || Utils.isEmpty(serverEndpoint.value())) {
            throw new IllegalArgumentException("Endpoint is not empty");
        }

        toStompWebSocketListener = new ToStompWebSocketListener(serverEndpoint.value());
    }

    @Override
    public WebSocketListener getWebSocketListener() {
        return toStompWebSocketListener;
    }

    public void addListener(StompListener... listeners) {
        toStompWebSocketListener.addListener(listeners);
    }
}
