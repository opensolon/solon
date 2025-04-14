package features.websse;

import demo.websse.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.noear.solon.Solon;
import org.noear.solon.net.http.HttpUtilsFactory;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;
import org.noear.solon.net.http.textstream.ServerSentEvent;
import org.noear.solon.rx.SimpleSubscriber;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/4/14 created
 */
@Timeout(5)
@SolonTest(App.class)
public class OkClientTest {
    private HttpUtilsFactory httpUtils = new OkHttpUtilsFactory();

    @Test
    public void ok_case1() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        httpUtils.http("http://localhost:" + Solon.cfg().serverPort() + "/test/sse1")
                .execAsSseStream("GET")
                .subscribe(new SimpleSubscriber<ServerSentEvent>()
                        .doOnNext(event -> {
                            System.out.println(event);
                            latch.countDown();
                        }).doOnComplete(latch::countDown));
        latch.await();
    }

    @Test
    public void ok_case2() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        httpUtils.http("http://localhost:" + Solon.cfg().serverPort() + "/test/sse2")
                .execAsSseStream("GET")
                .subscribe(new SimpleSubscriber<ServerSentEvent>()
                        .doOnNext(event -> {
                            System.out.println(event);
                            latch.countDown();
                        }).doOnComplete(latch::countDown));
        latch.await();
    }

    @Test
    public void ok_case3() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        httpUtils.http("http://localhost:" + Solon.cfg().serverPort() + "/test/sse3")
                .execAsSseStream("GET")
                .subscribe(new SimpleSubscriber<ServerSentEvent>()
                        .doOnNext(event -> {
                            System.out.println(event);
                            latch.countDown();
                        }).doOnComplete(latch::countDown));
        latch.await();
    }
}