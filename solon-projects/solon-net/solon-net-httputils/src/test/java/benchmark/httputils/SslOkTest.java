package benchmark.httputils;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

/**
 * @author noear 2024/10/4 created
 */
public class SslOkTest {
    static HttpUtils http(String url) {
        return OkHttpUtilsFactory.getInstance().http(url);
    }

    //1000a=> 1549, 1494, 1481, 1235, 1213, 1242; 1143, 1123
    //1000b=> 1188, 1173, 1131, 1137, 1207
    @Test
    public void performance() {
        String url = "http://localhost:8080/hello?name=solon";

        for (int i = 0; i < 10; i++) {
            http(url).get();
        }


        long start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            http(url).get();
        }
        long timespan = System.currentTimeMillis() - start;
        System.out.println("timespan: " + timespan);
    }
}
