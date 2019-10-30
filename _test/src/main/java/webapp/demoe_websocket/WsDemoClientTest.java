package webapp.demoe_websocket;

import java.net.URI;

public class WsDemoClientTest {
    public static void test() {
        try {
            Thread.sleep(1000);
            WsDemoClient client = new WsDemoClient(URI.create("ws://127.0.0.1:18080/websocket"));
            client.connect();

            while (!client.isOpen()) {
                System.out.println("还没有打开:" + client.getReadyState());
            }
            System.out.println("建立websocket连接");
            client.send("asd");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
