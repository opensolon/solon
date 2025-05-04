package features.httputils;

import features.jdkhttp.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/10/27 created
 */
@SolonTest(App.class)
public class DataOkTest {
    public static HttpUtils http(String url) {
        return  OkHttpUtilsFactory.getInstance().http(url);
    }

    @Test
    public void get() throws Exception {
        String rst = http("http://localhost:8080/hello?name=noear")
                .get();

        assert rst.equals("hello noear");
    }

    @Test
    public void get2() throws Exception {
        String rst = http("http://localhost:8080/hello")
                .data("name", "noear")
                .get();

        assert rst.equals("hello noear");
    }

    //okhttp 不支持
//    @Test
//    public void get2_body() throws Exception {
//        String rst = http("http://localhost:8080/body")
//                .data("tag", "hello")
//                .bodyOfTxt("noear")
//                .get();
//
//        assert rst.equals("hello:noear");
//    }

    @Test
    public void post() throws Exception {
        String rst = http("http://localhost:8080/hello")
                .data("name", "noear")
                .post();

        assert rst.equals("hello noear");
    }

    @Test
    public void postEmpty() throws Exception {
        String rst = http("http://localhost:8080/post")
                .post();

        assert rst.equals("ok");
    }
}
