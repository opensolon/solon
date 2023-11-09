package demo;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.Listener.PipelineWebSocketListener;
import org.noear.solon.net.websocket.Listener.RouterWebSocketListener;
import org.noear.solon.net.websocket.Listener.SimpleWebSocketListener;
import org.noear.solon.net.websocket.WebSocket;

import java.io.IOException;

/**
 * @author noear
 * @since 2.6
 */
@ServerEndpoint
public class WebSocketDemo3 extends RouterWebSocketListener {
    public WebSocketDemo3() {
        of("/admin", new SimpleWebSocketListener() {
            @Override
            public void onOpen(WebSocket socket) {
                //给管理频道做个签权
                super.onOpen(socket);
            }
        });
    }

    @Override
    public void onMessage(WebSocket socket, String text) throws IOException {
        super.onMessage(socket, text);

        //统一处理消息
    }
}
