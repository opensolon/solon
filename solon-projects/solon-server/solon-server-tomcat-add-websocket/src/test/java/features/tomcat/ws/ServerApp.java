package features.tomcat.ws;

import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

import java.io.IOException;

@ServerEndpoint("/demo")
public class ServerApp extends SimpleWebSocketListener {
    public static void main(String[] args) throws Exception {
        Solon.start(ServerApp.class, args, app -> {
            app.enableWebSocket(true);
        });
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        socket.send("收到" + text);
    }
}
