package labs;

import demo.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/8/30 created
 */
@SolonTest(App.class)
public class AppTest extends HttpTester {
    @Test
    public void test1() throws Exception {
        path("/t1?name=d").get();
    }

    @Test
    public void test2() throws Exception {
        path("/t2?name=d").get();
    }

    @Test
    public void test3() throws Exception {
        path("/t3?name=d").get();
    }
}
