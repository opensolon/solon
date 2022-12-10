package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2022/12/10 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpPathTest extends HttpTestBase {
    @Test
    public void test0() throws Exception {
        assert path("/demo2/path/test0").get().equals("ok");
        assert path("/demo2/path/test0/").get().equals("ok");
        assert path("/demo2/path/test0/a").get().equals("ok");
        assert path("/demo2/path/test0/a/b").get().equals("ok");
    }

    @Test
    public void test1() throws Exception {
        assert path("/demo2/path/test1").get().equals("ok");
        assert path("/demo2/path/test1/").get().equals("ok");
        assert path("/demo2/path/test1/a").get().equals("ok") == false;
        assert path("/demo2/path/test1/a/b").get().equals("ok") == false;
    }

    @Test
    public void test2() throws Exception {
        assert path("/demo2/path/test2").get().equals("ok");
        assert path("/demo2/path/test2/").get().equals("ok");
        assert path("/demo2/path/test2/a").get().equals("ok");
        assert path("/demo2/path/test2/a/b").get().equals("ok") == false;
    }
}
