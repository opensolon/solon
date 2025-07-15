package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2025/7/15 created
 */
@SolonTest(App.class)
public class HttpArgumentResolverTest  extends HttpTester {
    @Test
    public void case1() throws IOException {
        assert path("/demo2/argumentResolver/getUser").get().equals("{\"name\":\"solon\"}");
    }
}
