package features;

import okhttp3.Response;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/10/5 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(TestApp.class)
public class Test extends HttpTestBase {
    @org.junit.Test
    public void test1() throws Exception {
        Response resp = path("/healthz").exec("GET");
        System.out.println(resp.body().string());
        assert resp.code() == 200;
    }
}
