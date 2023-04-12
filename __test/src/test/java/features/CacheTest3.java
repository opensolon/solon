package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class CacheTest3 extends HttpTester {
    @Test
    public void test1() throws Exception{
        String rst = path("/cache3/").get();

        Thread.sleep(100);

        assert rst.equals(path("/cache3/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache3/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache3/").get());
    }

    @Test
    public void test2() throws Exception{
        String rst = path("/cache3/").get();

        Thread.sleep(100);
        assert rst.equals(path("/cache3/").get());

        rst = path("/cache3/update").get();
        assert rst.equals(path("/cache3/").get());

    }
}
