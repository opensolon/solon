package labs.case2;

import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear 2024/11/27 created
 */
@ServerEndpoint("/")
public class WsApp implements WebSocketListener {
    public static void main(String[] args) {
        Solon.start(WsApp.class, args, app -> {
            app.enableWebSocket(true);
        });
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
