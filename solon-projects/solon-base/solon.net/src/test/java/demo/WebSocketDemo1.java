package demo;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.Listener.SimpleWebSocketListener;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
@ServerEndpoint("/user/{userId}")
public class WebSocketDemo1 extends SimpleWebSocketListener {
    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        super.onMessage(socket, text);
    }
}
