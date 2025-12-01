package features.jetty.jsp;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 *
 * @author noear 2025/12/1 created
 *
 */
@SolonTest(App.class)
public class JettyJspTest extends HttpTester {
    @Test
    public void case1() {
        assert path("/").get().contains("你好");
    }
}
