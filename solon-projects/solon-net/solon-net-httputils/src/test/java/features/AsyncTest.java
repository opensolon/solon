package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsImpl;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsImpl;

import java.util.concurrent.CompletableFuture;

/**
 * @author noear 2024/10/6 created
 */
public class AsyncTest {
    @Test
    public void case11() throws Exception {
        CompletableFuture<String> htmlFuture = new CompletableFuture<>();
        new JdkHttpUtilsImpl("https://solon.noear.org/").getAsync(((isSuccessful, resp, error) -> {
            if (isSuccessful) {
                htmlFuture.complete(resp.bodyAsString());
            } else {
                htmlFuture.completeExceptionally(error);
            }
        }));

        System.out.println(htmlFuture.get());

        assert htmlFuture.get() != null;
        assert htmlFuture.get().contains("Solon");
    }

    @Test
    public void case12() throws Exception {
        CompletableFuture<String> htmlFuture = new CompletableFuture<>();
        new JdkHttpUtilsImpl("https://www.bilibili.com/").getAsync(((isSuccessful, resp, error) -> {
            if (isSuccessful) {
                htmlFuture.complete(resp.bodyAsString());
            } else {
                htmlFuture.completeExceptionally(error);
            }
        }));

        System.out.println(htmlFuture.get());

        assert htmlFuture.get() != null;
        assert htmlFuture.get().contains("bilibili");
    }

    @Test
    public void case21() throws Exception {
        CompletableFuture<String> htmlFuture = new CompletableFuture<>();
        new OkHttpUtilsImpl("https://solon.noear.org/").getAsync(((isSuccessful, resp, error) -> {
            if (isSuccessful) {
                htmlFuture.complete(resp.bodyAsString());
            } else {
                htmlFuture.completeExceptionally(error);
            }
        }));

        System.out.println(htmlFuture.get());

        assert htmlFuture.get() != null;
        assert htmlFuture.get().contains("Solon");
    }

    @Test
    public void case22() throws Exception {
        CompletableFuture<String> htmlFuture = new CompletableFuture<>();
        new OkHttpUtilsImpl("https://www.bilibili.com/").getAsync(((isSuccessful, resp, error) -> {
            if (isSuccessful) {
                htmlFuture.complete(resp.bodyAsString());
            } else {
                htmlFuture.completeExceptionally(error);
            }
        }));

        System.out.println(htmlFuture.get());

        assert htmlFuture.get() != null;
        assert htmlFuture.get().contains("bilibili");
    }
}
