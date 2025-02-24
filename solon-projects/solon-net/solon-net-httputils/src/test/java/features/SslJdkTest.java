package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtils;

/**
 * @author noear 2024/10/4 created
 */
public class SslJdkTest {
    static HttpUtils http(String url) {
        return new JdkHttpUtils(url);
    }


    @Test
    public void case11() throws Exception {
        String html = http("https://solon.noear.org/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("Solon");
    }

    @Test
    public void case12() throws Exception {
        String html = http("https://www.bilibili.com/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("bilibili");
    }
}
