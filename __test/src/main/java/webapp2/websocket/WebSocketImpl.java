package webapp2.websocket;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.WebSocketListener;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author noear 2023/11/23 created
 */
@ServerEndpoint
public class WebSocketImpl implements WebSocketListener {
    @Override
    public void onOpen(WebSocket socket) {
        System.out.println("::onOpen=" + socket.id());
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        System.out.println("::onMessage=" + socket.id());
    }

    @Override
    public void onMessage(WebSocket socket, ByteBuffer binary) throws IOException {
        System.out.println("::onMessage=" + socket.id());
    }

    @Override
    public void onClose(WebSocket socket) {
        System.out.println("::onClose=" + socket.id());
    }

    @Override
    public void onError(WebSocket socket, Throwable error) {
        System.out.println("::onError=" + socket.id());
    }
}
