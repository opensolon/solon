package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/6/13 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class GatewayTest extends HttpTester {
    @Test
    public void api_path() throws Exception {
        String rst = path("/demo53/api2/hello/solon").get();

        assert rst != null;
        assert "solon".equals(rst) || rst.contains("solon");
    }
}
