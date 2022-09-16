package features;

import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Utils;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author noear 2021/12/17 created
 */

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class Log4j2Test {

    @Test
    public void test1() {
        Logger log = LoggerFactory.getLogger(Log4j2Test.class);

        MDC.put("traceId", Utils.guid());

        log.info("你好");
        log.trace("你不好");
        log.debug("试试");

        MDC.put("tag0", "user_1");

        log.info("你好 {}!", "world!");
        log.warn("你不好 {}!", "world!");

        log.error("出错了", new IllegalStateException());
    }
}
