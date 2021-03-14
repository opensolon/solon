package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Utils;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.util.concurrent.CountDownLatch;

/**
 * @author noear 2021/3/14 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class BreakerTest extends HttpTestBase {

    @Override
    public boolean enablePrint() {
        return false;
    }

    @Test
    public void test() throws Exception {
        CountDownLatch downLatch = new CountDownLatch(5);

        for (int i = 0; i < 5; i++) {
            Utils.pools.submit(() -> {
                try {
                    path("/demox/test").get();

                } catch (Exception ex) {

                } finally {
                    downLatch.countDown();
                }
            });
        }

        downLatch.await();
    }
}
