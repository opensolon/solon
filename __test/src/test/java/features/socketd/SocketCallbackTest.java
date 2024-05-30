package features.socketd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.snack.ONode;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.client.ClientSession;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Message;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.socketd.transport.core.listener.PipelineListener;
import org.noear.socketd.transport.core.listener.SimpleListener;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@SolonTest(App.class)
public class SocketCallbackTest {
    @Test
    public void test_callback_message() throws Throwable {
        int _port = 8080 + 20000;

        ClientSession session = SocketD.createClient("tcp://localhost:"+ _port)
                .listen(new PipelineListener().next(new SimpleListener(){
                    @Override
                    public void onMessage(Session session, Message message) {
                        System.out.println("实例监到，收到了："+message);
                    }
                }).next(SocketdProxy.socketdToHandler))
                .openOrThow();

        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        Entity message = new StringEntity(map_josn)
                .metaPut("Content-Type", "application/json");


        CompletableFuture<Boolean> check = new CompletableFuture<>();
        session.sendAndSubscribe(root + "/demoh/rpc/hello", message).thenReply(r -> {
            String rst_str = ONode.deserialize(r.dataAsString());

            System.out.println("收到："+rst_str);

            check.complete("name=noear".equals(rst_str));
        });

        assert check.get(2, TimeUnit.SECONDS);
    }
}
