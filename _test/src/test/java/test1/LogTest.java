package test1;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author noear 2021/5/10 created
 */
@Slf4j
public class LogTest {
    @Test
    public void test(){
        log.trace("test");
    }
}
