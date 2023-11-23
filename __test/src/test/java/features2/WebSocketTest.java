package features2;

import org.junit.jupiter.api.Test;
import org.noear.java_websocket.client.SimpleWebSocketClient;

/**
 * @author noear 2023/11/23 created
 */
public class WebSocketTest {
    @Test
    public void test_close_slef() throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient("ws://127.0.0.1:8080");
        client.connectBlocking();
        client.close();
    }

    @Test
    public void test_close() throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient("ws://127.0.0.1:18080");
        client.connectBlocking();
        client.close();
    }


    @Test
    public void test_close_slef2() throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient("ws://127.0.0.1:8080");
        client.connectBlocking();
        //client.close();
    }

    @Test
    public void test_close2() throws Exception {
        SimpleWebSocketClient client = new SimpleWebSocketClient("ws://127.0.0.1:18080");
        client.connectBlocking();
        //client.close();
    }
}
