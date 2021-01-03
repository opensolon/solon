package features.socketd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.Nami;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.snack.ONode;
import org.noear.solon.core.message.Message;
import org.noear.solon.core.message.Session;
import org.noear.solon.socketd.SocketD;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoh_socketd.HelloRpcService;
import webapp.utils.ContentTypes;

import java.util.HashMap;
import java.util.Map;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketResponseTest {
    @Test
    public void test() throws Throwable{
        int _port = 8080 + 20000;

        Session session = SocketD.createSession("tcp://localhost:"+_port);


        String root = "tcp://localhost:" + _port;
        Message message =  Message.wrap(root + "/demog/中文/1",null, "Hello 世界!".getBytes());

        Message rst = session.sendAndResponse(message);

        System.out.println(rst.bodyAsString());

        assert "我收到了：Hello 世界!".equals(rst.bodyAsString());
    }

    @Test
    public void test_rpc_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SocketD.createSession("tcp://localhost:"+ _port);


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);

        Message message = Message.wrap(root + "/demoh/rpc/hello", ContentTypes.JSON, map_josn.getBytes());

        Message rst = session.sendAndResponse(message);
        String rst_str = ONode.deserialize(rst.bodyAsString());

        System.out.println("收到:" + rst_str);

        assert "name=noear".equals(rst_str);
    }

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = Nami.builder()
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> "tcp://localhost:" + _port)
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

}
