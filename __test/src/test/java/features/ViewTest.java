package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2021/7/22 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class ViewTest extends HttpTestBase {
    @Test
    public void test90() throws IOException {
        String rst = path("/demo9/view/json").get();
        assert  rst.indexOf("dock") > 0;
        assert  rst.indexOf("world") > 0;
    }

    @Test
    public void test91() throws IOException {
        String rst = path("/demo9/view/beetl").get();
        assert  rst.indexOf("beetl::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test92() throws IOException{
        String rst = path("/demo9/view/ftl").get();
        assert  rst.indexOf("ftl::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test93() throws IOException{
        String rst = path("/demo9/view/enjoy").get();
        assert  rst.indexOf("enjoy::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test94() throws IOException{
        String rst = path("/demo9/view/thymeleaf").get();
        assert  rst.indexOf("thymeleaf::") > 0;
        assert  rst.indexOf("你好") > 0;
    }

    @Test
    public void test95() throws IOException{
        String rst = path("/demo9/view/velocity").get();
        assert  rst.indexOf("velocity::") > 0;
        assert  rst.indexOf("你好") > 0;
    }
}
