package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Utils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author noear 2022/3/21 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class LogImpTest {
    @Test
    public void test1() {
        Logger log = LoggerFactory.getLogger(LogImpTest.class);

        MDC.put("traceId", Utils.guid());

        log.info("你好");
        log.trace("你不好");
        log.debug("试试");

        MDC.put("tag0", "user_1");

        log.info("你好 {}!", "world!");
        log.warn("你不好 {}!", "world!");

        log.error("出错了。{}", new RuntimeException());
    }
}
