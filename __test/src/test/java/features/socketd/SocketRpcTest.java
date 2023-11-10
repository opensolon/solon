package features.socketd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.Nami;
import org.noear.nami.channel.socketd.SocketdChannel;
import org.noear.solon.Solon;
import org.noear.socketd.SocketD;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demoh_socketd.HelloRpcService;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class SocketRpcTest {

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = SocketdChannel.create("tcp://localhost:" + _port, HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api2() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .url("tcp://localhost:" + (Solon.cfg().serverPort() + 20000) + "/demoh/rpc")
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api3() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "tcp://localhost:" + (Solon.cfg().serverPort() + 20000))
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_http1() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "http://localhost:" + Solon.cfg().serverPort())
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_ws1() throws Throwable {
        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "ws://localhost:" + (Solon.cfg().serverPort() + 15000))
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");
        System.out.println(rst);

        assert "name=noear".equals(rst);
    }
}
