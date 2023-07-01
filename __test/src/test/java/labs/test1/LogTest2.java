package test1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2021/5/10 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@Slf4j
public class LogTest2 {
    @Test
    public void test(){
        log.trace("test");
    }
}
