package features;

import okhttp3.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;
import java.util.List;

/**
 * @author noear 2022/3/30 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpSessionTest extends HttpTestBase {

    @Test
    public void session() throws IOException {
        //init
        Response response = path("/demo2/session/id").exec("GET");
        String session_id = response.body().string();
        List<String> cookies;
        StringBuilder cookiesStr;

        cookies = response.headers().values("Set-Cookie");
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }


        System.out.println(session_id);
        System.out.println(cookiesStr);


        //set
        response = path("/demo2/session/set").data("val", "test")
                .header("Cookie", cookiesStr.toString())
                .exec("POST");

        cookies = response.headers().values("Set-Cookie");
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }

        //get
        String rst = path("/demo2/session/get")
                .header("Cookie", cookiesStr.toString())
                .get();
        assert "test".equals(rst);
    }

    @Test
    public void session2() throws IOException {
        //init
        Response response = path("/demo2/session/id").exec("GET");
        String session_id = response.body().string();
        List<String> cookies;
        StringBuilder cookiesStr;

        cookies = response.headers().values("Set-Cookie");
        cookiesStr = new StringBuilder();
        for (String cookie : cookies) {
            String KeyVal = cookie.split(";")[0];
            System.out.println("cookie:: " + KeyVal);
            if (KeyVal.contains("=")) {
                cookiesStr.append(KeyVal).append("; ");
            }
        }


        System.out.println(session_id);
        System.out.println(cookiesStr);


        //set
        path("/demo2/session/set").data("val", "test")
                .exec("POST");


        //get（使用旧的 cookie）
        String rst = path("/demo2/session/get")
                .get();
        assert "test".equals(rst) == false;
    }
}
