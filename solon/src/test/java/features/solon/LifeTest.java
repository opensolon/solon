package features.solon;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author noear 2025/3/26 created
 */
public class LifeTest {
    @Test
    public void case1() {
        CountDownLatch latch = new CountDownLatch(3);

        AppContext ctx = new AppContext();
        ctx.lifecycle(() -> {
            latch.countDown();
            ctx.lifecycle(() -> {
                latch.countDown();
                ctx.lifecycle(() -> {
                    latch.countDown();
                });
            });
        });
        ctx.start();

        assert latch.getCount() == 0;
    }
}
