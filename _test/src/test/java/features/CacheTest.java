package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class CacheTest extends HttpTestBase {
    @Test
    public void test1() throws Exception{
        String rst = path("/cache/").get();

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        assert rst.equals(path("/cache/").get());
    }

    @Test
    public void test2() throws Exception{
        String rst = path("/cache/").get();

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());

        Thread.sleep(100);

        rst = path("/cache/update").get();
        assert rst.equals(path("/cache/").get());
    }

    @Test
    public void test3() throws Exception{
        String rst = path("/cache/").get();

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;

        Thread.sleep(100);

        path("/cache/remove").get();
        assert rst.equals(path("/cache/").get()) == false;
    }
}
