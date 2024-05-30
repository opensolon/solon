package demo;

import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.HttpUtils;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;

import java.util.List;

/**
 * @author noear 2022/1/18 created
 * @author liao.chunping 2022/08/22
 */
@SolonTest(DemoApp.class)
public class DemoTest extends HttpTester {
    @Test
    public void test() throws Exception {
        Response response = path("/put").data("id", "12").data("name", "world").exec("post");
        HttpUtils request = path("/get");
        request.header("Cookie", getCookie(response));
        String json = request.get();
        assert json.contains("12");
        assert json.contains("world");

        HttpUtils newRequest = path("/get");
        String newJson = newRequest.get();
        assert !newJson.contains("12");
        assert !newJson.contains("world");
    }

    /**
     * 获取 cookie
     *
     * @param response 响应
     * @return cookie
     */
    private String getCookie(Response response) {
        List<String> cookies = response.headers().values("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        for (String c1 : cookies) {
            String kv = c1.split(";")[0];
            if (kv.contains("=")) {
                sb.append(kv).append(";");
            }
        }
        return sb.toString();
    }
}
