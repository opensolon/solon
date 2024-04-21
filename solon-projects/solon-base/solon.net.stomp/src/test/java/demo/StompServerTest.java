package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Component;
import org.noear.solon.net.annotation.ServerEndpoint;
import org.noear.solon.net.stomp.Message;
import org.noear.solon.net.stomp.StompListener;
import org.noear.solon.net.stomp.ToStompWebSocketListener;
import org.noear.solon.net.websocket.WebSocket;

/**
 * @author noear
 * @since 2.4
 */
public class StompServerTest {
    public static void main(String[] args) {
        Solon.start(StompServerTest.class, args, app->{
            app.enableWebSocket(true);
        });
    }

    @ServerEndpoint("/chat")
    public class ChatToStompWebSocketListener extends ToStompWebSocketListener {

        public ChatToStompWebSocketListener() {
            super();

            Solon.context().getBeanAsync(StompListener.class, bean -> {
                setListener(bean);
            });
        }
    }

    @Component
    public class StompListenerImpl implements StompListener {

        @Override
        public void onOpen(WebSocket socket) {

        }

        @Override
        public void onConnect(WebSocket socket, Message message) {

        }

        @Override
        public void onClose(WebSocket socket) {

        }

        @Override
        public void onDisconnect(WebSocket socket, Message message) {

        }

        @Override
        public void onSubscribe(WebSocket socket, Message message) {

        }

        @Override
        public void onUnsubscribe(WebSocket socket, Message message) {

        }

        @Override
        public void onSend(WebSocket socket, Message message) {

        }

        @Override
        public void onAck(WebSocket socket, Message message) {

        }
    }
}
