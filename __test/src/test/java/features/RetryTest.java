package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/6/17 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class RetryTest extends HttpTester {
    @Test
    public void test1() throws Exception{
       assert  path("/demof/retry").execAsCode("GET") == 200;
    }

    @Test
    public void test2() throws Exception{
        assert  path("/demof/retry2").execAsCode("GET") == 500;
    }
}
