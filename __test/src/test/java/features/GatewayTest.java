package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/6/13 created
 */
@SolonTest(App.class)
public class GatewayTest extends HttpTester {
    @Test
    public void api_path() throws Exception {
        String rst = path("/demo53/api2/hello/solon").get();

        assert rst != null;
        assert "solon".equals(rst) || rst.contains("solon");
    }
}
