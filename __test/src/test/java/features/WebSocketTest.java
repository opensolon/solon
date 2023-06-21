package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SocketD;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoe_websocket.WsDemoClient;

import java.net.URI;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class WebSocketTest {
    @Test
    public void test1() throws Throwable {
        //
        //测试websocket框架
        //
        Thread.sleep(100);
        long time_start = System.currentTimeMillis();

        WsDemoClient client = new WsDemoClient(URI.create("ws://127.0.0.1:18080/demoe/websocket/12"));
        client.connect();

        while (!client.isOpen()) {
            if (System.currentTimeMillis() - time_start > 1000 * 2) {
                throw new RuntimeException("没有WebSocket服务或链接超时");
            }

            Thread.sleep(100);
            //System.out.println("还没有打开:" + client.getReadyState());
        }
        System.out.println("建立websocket连接");
        Exception errors = null;
        try {
            client.send("test0");
            client.send("test1");
            client.send("test2");
            client.send("test3");
        }catch (Exception e){
            errors = e;
        }

        assert errors == null;
        System.out.println("测试完成...");
    }

    @Test
    public void test_async_message3_ws() throws Throwable {
        Session session = SocketD.createSession("ws://127.0.0.1:18080/demoe/websocket/12", true);

        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.listener(new Listener() {
            @Override
            public void onMessage(Session session, Message message) {
                System.out.println("异步发送-ws::实例监到，收到了：" + message);
                check.complete(true);
            }
        });


        //异步发
        session.sendAsync("test0");
        session.sendAsync("test1");
        session.sendAsync("test2");
        session.sendAsync("test3");

        assert check.get(2, TimeUnit.SECONDS);

        session.close();

        Thread.sleep(1000);
    }
}
