package features.websse;

import demo.websse.App;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.noear.solon.Solon;
import org.noear.solon.net.http.HttpUtilsFactory;
import org.noear.solon.net.http.impl.jdk.JdkHttpUtilsFactory;
import org.noear.solon.net.http.impl.okhttp.OkHttpUtilsFactory;
import org.noear.solon.net.http.textstream.ServerSentEvent;
import org.noear.solon.rx.SimpleSubscriber;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/4/14 created
 */
@Timeout(15)
@SolonTest(App.class)
public class ClientTest {
    private HttpUtilsFactory jdkHttpUtils = new JdkHttpUtilsFactory();
    private HttpUtilsFactory okHttpUtils = new OkHttpUtilsFactory();

    @Test
    public void jdk_case1() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        jdkHttpUtils.http("http://localhost:" + Solon.cfg().serverPort() + "/sse/rx")
                .execAsSseStream("GET")
                .subscribe(new SimpleSubscriber<ServerSentEvent>()
                        .doOnNext(event -> {
                            System.out.println(event);
                        }).doOnComplete(latch::countDown));
        latch.await();
    }

    @Test
    public void ok_case1() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);

        okHttpUtils.http("http://localhost:" + Solon.cfg().serverPort() + "/sse/rx")
                .execAsSseStream("GET")
                .subscribe(new SimpleSubscriber<ServerSentEvent>()
                        .doOnNext(event -> {
                            System.out.println(event);
                        }).doOnComplete(latch::countDown));
        latch.await();
    }
}