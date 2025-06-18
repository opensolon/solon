package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear 2024/10/6 created
 */
public class AsyncJdkTest {
    static HttpUtils http(String url) {
        return JdkHttpUtilsFactory.getInstance().http(url);
    }

    @Test
    public void case11() throws Exception {
        CompletableFuture<HttpResponse> htmlFuture = http("https://solon.noear.org/").execAsync("GET");

        String text = htmlFuture.get().bodyAsString();
        System.out.println(text);

        assert text != null;
        assert text.contains("Solon");
    }

    @Test
    public void case12() throws Exception {
        CompletableFuture<HttpResponse> htmlFuture = http("https://www.bilibili.com/").execAsync("GET");

        String text = htmlFuture.get().bodyAsString();
        System.out.println(text);

        assert text != null;
        assert text.contains("bilibili");
    }
}
