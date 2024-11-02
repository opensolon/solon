package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/11/2 created
 */
@SolonTest(App.class)
public class HttpIndexTest extends HttpTester {
    @Test
    public void case1() throws Exception {
        assert "a1-1".equals(path("/demo1/a1/a").get());
    }

    @Test
    public void case2() throws Exception {
        assert "b1-2-1".equals(path("/demo1/b1/a").get());
    }
}
