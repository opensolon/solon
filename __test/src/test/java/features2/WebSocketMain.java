package features2;

import org.noear.java_websocket.client.SimpleWebSocketClient;

/**
 * @author noear 2023/11/23 created
 */
public class WebSocketMain {
    public static void main(String[] args) throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient("ws://127.0.0.1:8080");
        client.connectBlocking();
        //client.close();
    }
}
