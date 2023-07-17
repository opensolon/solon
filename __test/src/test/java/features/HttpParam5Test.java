package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2021/1/5 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HttpParam5Test extends HttpTester {
    @Test
    public void test1() throws Exception{
       String rst = path("/demo2/param5/test1?a=1&params[a]=2").get();

       assert "1:2".equals(rst);
    }

    @Test
    public void test2() throws Exception{
        String rst = path("/demo2/param5/test2?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test2?cat=1").get();

        assert "demo1".equals(rst);
    }

    @Test
    public void test3() throws Exception{
        String rst = path("/demo2/param5/test3?cat=2").get();

        assert "demo2".equals(rst);

        rst = path("/demo2/param5/test3?cat=1").get();

        assert "demo1".equals(rst);
    }
}
