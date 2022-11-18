package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2020/12/24 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpTest2 extends HttpTestBase {
    @Test
    public void test1() throws IOException {
        assert path("/demo1/run1/*?@=1").get().equals("http://localhost:8080/demo1/run1/*");
    }

    @Test
    public void test2() throws IOException {
        assert path("/demo1/run3/*?@=1").get().equals("@=1");
    }

    @Test
    public void test404() throws IOException {
        assert path("/demo1/hello/").head() == 404;
    }

//    @Test
//    public void test3() throws IOException {
//        if (Utils.loadClass("javax.servlet.http.HttpServletRequest") != null) {
//            assert path("/demo2/servlet/hello?name=noear").get().equals("Ok");
//        }
//    }

}
