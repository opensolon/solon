package features;

import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.cloud.extend.health.HealthChecker;
import org.noear.solon.core.handle.Result;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/10/5 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest extends HttpTestBase {
    @Test
    public void test1() throws Exception {
        Response resp = path("/healthz").exec("GET");
        System.out.println(resp.body().string());
        assert resp.code() == 200;
    }

    @Test
    public void test2() throws Exception {
        HealthChecker.addPoint("preflight", Result::succeed);
        HealthChecker.addPoint("test", Result::failure);


        Response resp = path("/healthz").exec("GET");
        System.out.println(resp.body().string());
        assert resp.code() == 503;
    }

    @Test
    public void test3() throws Exception {
        HealthChecker.addPoint("preflight", Result::succeed);
        HealthChecker.addPoint("test", Result::failure);
        HealthChecker.addPoint("boom", () -> {
            throw new IllegalStateException();
        });

        int code = path("/healthz").head();
        assert code == 500;
    }
}
