package features;

import okhttp3.Headers;
import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.List;

/**
 * @author noear 2021/2/15 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class SessionTest extends HttpTestBase {
    @Test
    public void test() throws Exception {
        Response response = path("/demob/session/setval").exec("get");
        System.out.println(response.body().string());

        List<String> cookies = response.headers().values("Set-Cookie");

        HttpUtils httpUtils = path("/demob/session/getval");

        StringBuilder sb = new StringBuilder();
        for (String c1 : cookies) {
            String kv = c1.split(";")[0];
            if (kv.contains("=")) {
                sb.append(kv).append(";");
            }
        }

        if(sb.length() > 0) {
            System.out.println(sb.toString());
            httpUtils.header("Cookie", sb.toString());
        }

        String html = httpUtils.get();

        assert html.contains("121212");
    }
}
