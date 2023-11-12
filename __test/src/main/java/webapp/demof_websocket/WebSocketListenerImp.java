package webapp.demof_websocket;

import org.noear.nami.channel.socketd.SocketdRpc;
import org.noear.socketd.transport.core.ConfigImpl;
import org.noear.solon.Solon;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.listener.RouterWebSocketListener;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;
import org.noear.solon.net.websocket.socketd.ToSocketdWebSocketListener;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint
public class WebSocketListenerImp extends PipelineWebSocketListener {
    public WebSocketListenerImp() {
        next(new SimpleWebSocketListener() {
            private Set<WebSocket> sessionMap = new HashSet<>();

            @Override
            public void onMessage(WebSocket session, String message) {
                System.out.println(session.getPath());

                if (Solon.cfg().isDebugMode()) {
                    return;
                }

                sessionMap.forEach(s -> {
                    s.send(message + "-" + sessionMap.size() + "-" + session.getParamMap());
                });
            }

            @Override
            public void onOpen(WebSocket session) {
                sessionMap.add(session);
            }

            @Override
            public void onClose(WebSocket session) {
                sessionMap.remove(session);
            }
        }).next(new RouterWebSocketListener().of("/demof/websocket/{id}", new SimpleWebSocketListener() {
            @Override
            public void onMessage(WebSocket socket, String text) throws IOException {
                socket.send("你好");
            }
        })).next(new ToSocketdWebSocketListener(new ConfigImpl(false), SocketdRpc
                .socketdToHandler));
    }
}
