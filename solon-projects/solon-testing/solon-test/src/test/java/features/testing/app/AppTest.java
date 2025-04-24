package features.testing.app;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/1/8 created
 */
@SolonTest
public class AppTest {
    @Inject
    private DemoCom demoCom;

    @Test
    public void test(){
        System.out.println(demoCom.hello());
    }
}
