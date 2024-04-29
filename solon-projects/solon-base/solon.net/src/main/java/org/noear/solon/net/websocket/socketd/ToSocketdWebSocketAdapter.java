package org.noear.solon.net.websocket.socketd;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;

/**
 * @author noear
 * @since 2.7
 */
public abstract class ToSocketdWebSocketAdapter implements WebSocketListenerSupplier, Listener {
    private ToSocketdWebSocketListener webSocketListener;

    @Override
    public WebSocketListener getWebSocketListener() {
        if (webSocketListener == null) {
            webSocketListener = new ToSocketdWebSocketListener(new ConfigDefault(false), this);
        }

        return webSocketListener;
    }
}
