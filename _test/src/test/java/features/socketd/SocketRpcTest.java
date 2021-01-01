package features.socketd;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.Nami;
import org.noear.solon.extend.socketd.SocketD;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demoh_socketd.HelloRpcService;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SocketRpcTest {

    @Test
    public void test_rpc_api() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = SocketD.create("tcp://localhost:" + _port, HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api2() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = Nami.builder()
                .uri("tcp://localhost:" + _port + "/demoh/rpc")
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api3() throws Throwable {
        int _port = 8080 + 20000;

        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "tcp://localhost:" + _port)
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }

    @Test
    public void test_rpc_api_http1() throws Throwable {
        int _port = 8080;

        HelloRpcService rpc = Nami.builder()
                .upstream(() -> "http://localhost:" + _port)
                .create(HelloRpcService.class);

        String rst = rpc.hello("noear");

        System.out.println(rst);

        assert "name=noear".equals(rst);
    }
}
