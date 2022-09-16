package features;

import org.junit.Test;
import org.noear.weed.utils.ThrowableUtils;

/**
 * @author noear 2021/6/1 created
 */
public class ThrowTest {
    @Test
    public void test1() {
        RuntimeException ex = new RuntimeException();

        assert ex == ThrowableUtils.throwableUnwrap(ex);
    }
}
