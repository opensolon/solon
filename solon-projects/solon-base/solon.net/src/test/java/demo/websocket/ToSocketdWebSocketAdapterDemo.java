package demo.websocket;

import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketAdapter;

import java.io.IOException;

/**
 * @author noear
 * @since 2.7
 */
public class ToSocketdWebSocketAdapterDemo extends ToSocketdWebSocketAdapter {
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
