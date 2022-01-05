package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoe_websocket.WsDemoClient;

import java.net.URI;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class WebSocketTestSelf {
    @Test
    public void test1() throws Throwable {
        //
        // 测试 jetty or undertow 自带的 websocket
        //
        Thread.sleep(100);
        long time_start = System.currentTimeMillis();

        WsDemoClient client = new WsDemoClient(URI.create("ws://127.0.0.1:8080/demoe/websocket/12"));
        client.connect();

        while (!client.isOpen()) {
            if (System.currentTimeMillis() - time_start > 1000 * 2) {
                throw new RuntimeException("没有自带WebSocket服务或链接超时");
            }

            Thread.sleep(100);
            //System.out.println("还没有打开:" + client.getReadyState());
        }
        System.out.println("建立websocket连接");
        client.send("dddc");

        Thread.sleep(100);
    }
}
