package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.RunUtil;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2025/3/25 created
 */
public class AsyncTest {
    @Test
    public void case1() {
        CountDownLatch latch = new CountDownLatch(3);
        RunUtil.async(() -> {
            latch.countDown();
            RunUtil.async(() -> {
                latch.countDown();
                RunUtil.async(() -> {
                    latch.countDown();
                });
            });
        });

        try {
            latch.await(1, TimeUnit.SECONDS);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }

        assert latch.getCount() == 0;
    }
}