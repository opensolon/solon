package testapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.ServletApp;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(ServletApp.class)
public class ServletTest extends HttpTestBase {
    @Override
    public boolean enablePrint() {
        return true;
    }

    @Test
    public void test1_f_ok() throws Exception{
        assert  path("/hello/").get().equals("OK");
        assert  path("/hello/a").get().equals("OK");
        assert  path("/hello/a/a").get().equals("OK");
    }

    @Test
    public void test1_f_no() throws Exception{
        assert  path("/hello2/").get().equals("OK") == false;
        assert  path("/hello2/a").get().equals("OK") == false;
        assert  path("/hello2/a/a").get().equals("OK") == false;
    }


    @Test
    public void test2_s_ok() throws Exception{
        assert  path("/heihe/").get().equals("OK");
        assert  path("/heihe/a").get().equals("OK");
        assert  path("/heihe/a/a").get().equals("OK");
    }

    @Test
    public void test2_s_no() throws Exception{
        assert  path("/heihe2/").get().equals("OK") == false;
        assert  path("/heihe2/a").get().equals("OK") == false;
        assert  path("/heihe2/a/a").get().equals("OK") == false;
    }
}
