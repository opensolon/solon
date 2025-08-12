package features.httputils;

import features.jdkhttp.App;
import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpResponse;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author noear 2024/10/27 created
 */
@SolonTest(App.class)
public class DataJdkTest {
    public static HttpUtils http(String url) {
        return JdkHttpUtilsFactory.getInstance().http(url);
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

    @Test
    public void get2_body() throws Exception {
        String rst = http("http://localhost:8080/body")
                .data("tag", "hello")
                .bodyOfTxt("noear")
                .get();

        assert rst.equals("hello:noear");
    }

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

    @Test
    public void redirect_case301() throws Exception {
        assert http("http://localhost:8080/redirect/jump?code=301").get().equals("ok");
    }

    @Test
    public void redirect_case302() throws Exception {
        assert http("http://localhost:8080/redirect/jump?code=302").get().equals("ok");
    }

    @Test
    public void redirect_case307() throws Exception {
        assert http("http://localhost:8080/redirect/jump?code=307").get().equals("ok");
    }


    @Test
    public void redirect_case301b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/jump?code=301").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }

    @Test
    public void redirect_case302b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/jump?code=302").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }

    @Test
    public void redirect_case307b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/jump?code=307").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }

    /// ////////////////////

    @Test
    public void redirect_h5_case301() throws Exception {
        HttpResponse resp = http("http://localhost:8080/redirect/h5?code=301").exec("GET");

        System.out.println("code:" + resp.code());
        String html = resp.bodyAsString();
        System.out.println(html);
        assert html.contains("html");
    }

    @Test
    public void redirect_h5_case302() throws Exception {
        HttpResponse resp = http("http://localhost:8080/redirect/h5?code=302").exec("GET");

        System.out.println("code:" + resp.code());
        String html = resp.bodyAsString();
        System.out.println(html);
        assert html.contains("html");
    }

    @Test
    public void redirect_h5_case307() throws Exception {
        HttpResponse resp = http("http://localhost:8080/redirect/h5?code=307").exec("GET");

        System.out.println("code:" + resp.code());
        String html = resp.bodyAsString();
        System.out.println(html);
        assert html.contains("html");
    }


    @Test
    public void redirect_h5_case301b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/h5?code=301").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }

    @Test
    public void redirect_h5_case302b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/h5?code=302").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }

    @Test
    public void redirect_h5_case307b() throws Exception {
        AtomicInteger codeHolder = new AtomicInteger();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        http("http://localhost:8080/redirect/h5?code=307").execAsync("GET")
                .thenAccept(resp -> {
                    codeHolder.set(resp.code());
                    countDownLatch.countDown();
                });
        countDownLatch.await();

        assert codeHolder.get() == 200;
    }
}
