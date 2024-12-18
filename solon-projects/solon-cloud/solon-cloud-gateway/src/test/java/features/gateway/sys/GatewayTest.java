package features.gateway.sys;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/10/1 created
 */
@SolonTest
public class GatewayTest extends HttpTester {
    static SimpleSolonApp solonApp;

    @BeforeAll
    public static void bef() throws Throwable {
        solonApp = new SimpleSolonApp(GatewayTest.class, "--cfg=sys.yml");
        solonApp.start(null);
    }

    @Test
    public void hello() throws Exception {
        assert "hello".equals(path("/hello").get());
    }

    @Test
    public void gateway_hello() throws Exception {
        assert "hello".equals(path("/test/hello").get());
    }
}
