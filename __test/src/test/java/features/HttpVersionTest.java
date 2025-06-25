package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2025/6/25 created
 */
@SolonTest(App.class)
public class HttpVersionTest extends HttpTester {
    @Test
    public void test0() throws IOException {
        assert path("/demo2/version/api")
                .header("Api-Version","1.0")
                .get().contains("v1.0");
    }

    @Test
    public void test0_1() throws IOException {
        assert path("/demo2/version/api")
                .header("Api-Version","2.0")
                .get().contains("v2.0");
    }
}
