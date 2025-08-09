package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpException;
import org.noear.solon.net.http.HttpExtension;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

import java.io.IOException;

/**
 * @author noear 2024/10/4 created
 */
public class SslJdkTest {
    static HttpUtils http(String url) {
        return  JdkHttpUtilsFactory.getInstance().http(url);
    }

    @Test
    public void caseSslError() {
        HttpException exception = Assertions.assertThrows(HttpException.class, () -> {
            http("https://www.baidu.com").ssl(new SslErrorSupplier()).get();
        });

        Assertions.assertThrows(NullPointerException.class, () -> {
            throw exception.getCause();
        });
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
