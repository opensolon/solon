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

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketCallbackTest {
    @Test
    public void test_callback_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SocketD.createSession("tcp://localhost:"+ _port, true);

        session.listener(new Listener() {
            @Override
            public void onMessage(Session session, Message message) {
                System.out.println("实例监到，收到了："+message);
            }
        });

        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        Message message = Message.wrap(root + "/demoh/rpc/hello","Content-Type=application/json", map_josn);


        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.sendAndCallback(message, (rst, err) -> {
            String rst_str = ONode.deserialize(rst.bodyAsString());

            System.out.println("收到："+rst_str);

            check.complete("name=noear".equals(rst_str));
        });

        assert check.get(2, TimeUnit.SECONDS);
    }
}
