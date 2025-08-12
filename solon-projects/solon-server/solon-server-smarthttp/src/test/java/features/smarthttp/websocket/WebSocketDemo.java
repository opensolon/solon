package features.smarthttp.websocket;

import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.websocket.WebSocket;
import org.noear.solon.net.websocket.listener.SimpleWebSocketListener;

@ServerEndpoint("/")
public class WebSocketDemo extends SimpleWebSocketListener {
    @Override
    public void onOpen(WebSocket socket) {

    }

    @Override
    public void onMessage(WebSocket socket, String text) {
        System.out.println("当前线程是否是虚拟线程: " + ThreadUtil.isVirtualThread());
        socket.send("我收到了：" + text);
    }
}