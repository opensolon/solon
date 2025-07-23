package features;

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

    @Test
    public void case13() throws Exception {
        String html = http("https://www.taobao.com/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("taobao");
    }

    @Test
    public void case14() throws Exception {
        String html = http("https://www.csdn.net/").get();
        System.out.println(html);

        assert html != null;
        assert html.contains("csdn");
    }
}
