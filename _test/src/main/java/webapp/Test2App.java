package webapp;

import org.noear.fairy.Fairy;
import org.noear.fairy.channel.xsocket.SocketChannel;
import org.noear.fairy.decoder.SnackDecoder;
import org.noear.fairy.encoder.SnackEncoder;
import org.noear.solon.Solon;
import org.noear.solon.core.message.Session;
import org.noear.solon.extend.xsocket.SessionFactory;
import webapp.demoh_xsocket.HelloRpcService;

public class Test2App {
    public static void main(String[] args) {
        Solon.start(TestApp.class, args, x -> x.enableHttp(false));

        int _port = 8080 + 20000;

        Session session = SessionFactory.create("localhost", _port, true);
        SocketChannel channel = new SocketChannel(session);

        HelloRpcService rpc = Fairy.builder()
                .encoder(SnackEncoder.instance)
                .decoder(SnackDecoder.instance)
                .upstream(() -> "tcp://localhost:" + _port)
                .channel(channel)
                .create(HelloRpcService.class);

        while (true) {
            try {
                Thread.sleep(100);
                test_rpc_api(rpc);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    public static void test_rpc_api(HelloRpcService rpc) throws Throwable {
        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }
}
