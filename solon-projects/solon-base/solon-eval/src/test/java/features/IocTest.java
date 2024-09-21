package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.eval.Soal;
import org.noear.solon.test.SolonTest;

/**
 * @author noear
 * @since 3.0
 */
@SolonTest
public class IocTest {
    @Test
    public void test() throws Exception {
        System.out.println(Soal.eval("Solon.app() != null"));
    }
}
