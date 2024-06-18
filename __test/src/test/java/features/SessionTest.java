package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.util.List;

/**
 * @author noear 2021/2/15 created
 */
@SolonTest(App.class)
public class SessionTest extends HttpTester {
    @Test
    public void test() throws Exception {
        HttpResponse response = path("/demob/session/setval").exec("get");
        System.out.println(response.bodyAsString());

        List<String> cookies = response.cookies();

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
