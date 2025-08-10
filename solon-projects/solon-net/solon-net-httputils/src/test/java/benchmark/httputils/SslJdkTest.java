package benchmark.httputils;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

/**
 * @author noear 2024/10/4 created
 */
public class SslJdkTest {
    static HttpUtils http(String url) {
        return JdkHttpUtilsFactory.getInstance().http(url);
    }

    //1000=> 1128, 837, 920, 911
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
