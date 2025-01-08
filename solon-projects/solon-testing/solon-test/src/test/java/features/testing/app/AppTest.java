package features.testing.app;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2025/1/8 created
 */
@SolonTest
public class AppTest {
    private final DemoCom demoCom;

    public AppTest(DemoCom demoCom){
        this.demoCom = demoCom;
    }

    @Test
    public void test(){
        System.out.println(demoCom.hello());
    }
}
