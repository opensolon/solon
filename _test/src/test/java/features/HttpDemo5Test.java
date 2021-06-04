package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2021/6/4 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpDemo5Test extends HttpTestBase {

    @Test
    public void test51() throws IOException {
        assert path("/demo5/rpctest/").get().equals("{\"SocketChannel\":\"SOCKET::test1=12\",\"HttpChannel\":\"POST::test1=12\"}");
    }
}
