package features.socketd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.core.message.Listener;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SocketD;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2022/3/11 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketAsyncTest {
    @Test
    public void test_async_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SocketD.createSession("tcp://localhost:" + _port, true);

        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.listener(new Listener() {
            @Override
            public void onMessage(Session session, Message message) {
                System.out.println("异步发送::实例监到，收到了：" + message);
                check.complete(true);
            }
        });

        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        //异步发
        Message message = Message.wrap(root + "/demoh/rpc/hello", "Content-Type=application/json", map_josn);
        session.sendAsync(message);

        assert check.get(2, TimeUnit.SECONDS);
    }

    @Test
    public void test_async_message2() throws Throwable {
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
        session.sendAsync("test");

        assert check.get(2, TimeUnit.SECONDS);
    }

    @Test
    public void test_async_message3_self_ws() throws Throwable {
        Session session = SocketD.createSession("ws://127.0.0.1:8080/demoe/websocket/12", true);

        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.listener(new Listener() {
            @Override
            public void onMessage(Session session, Message message) {
                System.out.println("异步发送-ws-self::实例监到，收到了：" + message);
                check.complete(true);
            }
        });


        //异步发
        session.sendAsync("test");

        assert check.get(2, TimeUnit.SECONDS);
    }
}
