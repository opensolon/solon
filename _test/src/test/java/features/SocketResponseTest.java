package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.fairy.Fairy;
import org.noear.fairy.channel.xsocket.SocketChannel;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.snack.ONode;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoh_xsocket.HelloRpcService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketResponseTest {
    @Test
    public void test() throws Throwable{
        int _port = 8080 + 20000;

        Session session = SessionFactory.create("localhost",_port, true);


        String root = "tcp://localhost:" + _port;
        Message message =  Message.wrap(root + "/demog/中文/1", "Hello 世界!".getBytes());

        Message rst = session.sendAndResponse(message);

        System.out.println(rst.toString());

        assert "我收到了：Hello 世界!".equals(rst.toString());
    }

    @Test
    public void test_rpc_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SessionFactory.create("localhost", _port, true);


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        Message message = Message.wrap(root + "/demoe/rpc/hello", map_josn.getBytes());

        Message rst = session.sendAndResponse(message);
        String rst_str = ONode.deserialize(rst.toString());

        System.out.println("收到:" + rst_str);

        assert "name=noear".equals(rst_str);
    }

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SessionFactory.create("localhost",_port, true);
        SocketChannel channel = new SocketChannel(()->session);

        HelloRpcService rpc = Fairy.builder()
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> "tcp://localhost:" + _port)
                .channel(channel)
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

}
