package features.webrx;

import demo.webrx.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/30 created
 */
@SolonTest(value = App.class, args = "--server.port=7071")
public class Case3AppTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        assert path("/case3/m1?name=d").get().equals("Hello d");
    }

    @Test
    public void test2() throws Exception {
        assert path("/case3/m2?name=d").get().equals("Hello d");
    }
}