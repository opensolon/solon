package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoe_websocket.WsDemoClient;

import java.net.URI;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class WebSocketTest {
    @Test
    public void test() throws Throwable {
        Thread.sleep(2000);
        WsDemoClient client = new WsDemoClient(URI.create("ws://127.0.0.1:18080/demoe/websocket"));
        client.connect();

        while (!client.isOpen()) {
            Thread.sleep(100);
            //System.out.println("还没有打开:" + client.getReadyState());
        }
        System.out.println("建立websocket连接");
        client.send("asd");
    }
}
