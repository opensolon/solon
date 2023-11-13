package features.socketd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.Nami;
import org.noear.nami.channel.socketd.SocketdProxy;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.nami.coder.snack3.SnackEncoder;
import org.noear.snack.ONode;
import org.noear.socketd.SocketD;
import org.noear.socketd.transport.core.Entity;
import org.noear.socketd.transport.core.Session;
import org.noear.socketd.transport.core.entity.StringEntity;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoh_socketd.HelloRpcService;
import webapp.utils.ContentTypes;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class SocketResponseTest {
    @Test
    public void test() throws Throwable{
        int _port = 8080 + 20000;

        Session session = SocketD.createClient("tcp://localhost:"+_port)
                .listen(SocketdProxy.socketdToHandler)
                .open();


        String root = "tcp://localhost:" + _port;

        Entity rst = session.sendAndRequest(root + "/demog/中文/1", new StringEntity("Hello 世界!"));

        String tmp = rst.dataAsString();
        System.out.println(tmp);

        assert "我收到了：Hello 世界!".equals(tmp);
    }

    @Test
    public void test_rpc_message() throws Throwable {
        int _port = 8080 + 20000;

        Session session = SocketD.createClient("tcp://localhost:"+ _port)
                .listen(SocketdProxy.socketdToHandler)
                .open();


        String root = "tcp://localhost:" + _port;
        Map<String, Object> map = new HashMap<>();
        map.put("name", "noear");
        String map_josn = ONode.stringify(map);


        Entity rst = session.sendAndRequest(root + "/demoh/rpc/hello",
                new StringEntity(map_josn).metaString(ContentTypes.JSON));
        String rst_str = ONode.deserialize(rst.dataAsString());

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
