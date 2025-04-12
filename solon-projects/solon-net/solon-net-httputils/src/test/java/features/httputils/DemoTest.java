package features.httputils;

import org.junit.jupiter.api.Test;
import org.noear.solon.net.http.HttpUtils;
import org.noear.solon.rx.SimpleSubscriber;

import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2025/3/25 created
 */
public class DemoTest {
    @Test
    public void case1() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        HttpUtils.http("http://localhost:8080/test/functioncall/sse")
                .data("prompt", "今天杭州的天气情况？")
                .execAsLineStream("GET")
                .subscribe(new SimpleSubscriber<String>()
                        .doOnSubscribe(subscription -> {
                            System.out.println("开始");
                            subscription.request(Long.MAX_VALUE);
                        })
                        .doOnNext(item -> {
                            System.out.println(item);
                        }).doOnComplete(() -> {
                            System.out.println("完成");
                            System.out.println("===================================");
                            latch.countDown();
                        }).doOnError(err -> {
                            err.printStackTrace();
                            latch.countDown();
                        }));

        latch.await();
    }
}
