package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.socketd.SessionFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketCallbackTest {
    @Test
    public void test_callback_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SessionFactory.create("localhost", _port, true);


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        Message message = Message.wrapJson(root + "/demoe/rpc/hello", map_josn.getBytes());


        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.sendAndCallback(message, (rst, err) -> {
            String rst_str = ONode.deserialize(rst.toString());

            System.out.println("收到："+rst_str);

            check.complete("name=noear".equals(rst_str));
        });

        assert check.get();
    }
}
