package features;


import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HeaderTest extends HttpTestBase {
    @Test
    public void test1() throws Exception {
        assert path("/demo1/run0/?str=").get().equals("不是null(ok)");

        Map<String, String> map = new LinkedHashMap<>();
        map.put("address", "192.168.1.1:9373");
        map.put("service", "wateradmin");
        map.put("meta", "");
        map.put("check_type", "0");
        map.put("is_unstable", "0");
        map.put("check_url", "/run/check/");

        assert path("/demo2/header/")
                .header("Water-Trace-Id", "")
                .header("Water-From", "wateradmin@192.168.1.1:9373")
                .data(map).post().equals("");
    }

    @Test
    public void test2() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("address", "192.168.1.1:9373");
        map.put("service", "wateradmin");
        map.put("meta", "");
        map.put("check_type", "0");
        map.put("is_unstable", "0");
        map.put("check_url", "/run/check/");

        assert path("/demo2/header/")
                .header("Water-Trace-Id", "a")
                .header("Water-From", "wateradmin@192.168.1.1:9373")
                .data(map).post().equals("a");
    }

    @Test
    public void test3() throws Exception {
        Response res = path("/demo2/cookie/").exec("GET");

        List<String> tmp = res.headers("Set-Cookie");

        assert tmp.size() >= 2;
    }
}
