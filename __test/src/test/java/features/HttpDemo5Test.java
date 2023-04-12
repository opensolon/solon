package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2021/6/4 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HttpDemo5Test extends HttpTester {

    @Test
    public void test51() throws IOException {
        assert path("/demo5/rpctest/").get().equals("{\"SocketChannel\":\"SOCKET::test1=12\",\"HttpChannel\":\"POST::test1=12\"}");
    }
}
