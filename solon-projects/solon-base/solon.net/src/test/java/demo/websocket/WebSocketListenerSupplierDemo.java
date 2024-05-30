package demo.websocket;

import org.noear.socketd.transport.core.Listener;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.impl.ConfigDefault;
import org.noear.solon.net.websocket.WebSocketListener;
import org.noear.solon.net.websocket.WebSocketListenerSupplier;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

import java.io.IOException;

/**
 * @author noear
 * @since 2.7
 */
public class WebSocketListenerSupplierDemo implements WebSocketListenerSupplier, Listener {
    private ToSocketdWebSocketListener webSocketListener;

    @Override
    public WebSocketListener getWebSocketListener() {
        if (webSocketListener == null) {
            webSocketListener = new ToSocketdWebSocketListener(new ConfigDefault(false), this);
        }

        return webSocketListener;
    }

    @Override
    public void onOpen(Session session) throws IOException {

    }

    @Override
    public void onMessage(Session session, Message message) throws IOException {

    }

    @Override
    public void onClose(Session session) {

    }

    @Override
    public void onError(Session session, Throwable error) {

    }
}
