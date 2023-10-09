package test1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit5Extension;

/**
 * @author noear 2021/5/10 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@Slf4j
public class LogTest2 {
    @Test
    public void test(){
        log.trace("test");
    }
}
