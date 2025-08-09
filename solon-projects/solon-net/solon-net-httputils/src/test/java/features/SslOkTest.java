package features;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpException;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;

import java.util.function.Predicate;

/**
 * @author noear 2024/10/4 created
 */
public class SslOkTest {
    static HttpUtils http(String url) {
        return OkHttpUtilsFactory.getInstance().http(url);
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
    public void caseSslOk() {
        testUrlDo("https://solon.noear.org/", html -> html.contains("Solon"));
        testUrlDo("https://www.bilibili.com/", html -> html.contains("bilibili"));
        testUrlDo("https://www.taobao.com/", html -> html.contains("taobao"));
        testUrlDo("https://www.csdn.net/", html -> html.contains("csdn"));
        testUrlDo("https://i.cnblogs.com/", html -> html.contains("cnblogs"));
        testUrlDo("https://www.guancha.cn/", html -> html.contains("guancha"));
    }

    private void testUrlDo(String url, Predicate<String> test) {
        System.out.println("------------test do: " + url);

        String html = http(url).get();
        System.out.println(html);

        Assertions.assertNotNull(html);
        Assertions.assertTrue(test.test(html), "test failed:" + url);
    }
}
