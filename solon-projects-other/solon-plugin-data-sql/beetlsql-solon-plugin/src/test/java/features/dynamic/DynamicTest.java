package features.dynamic;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DynamicApp.class)
public class DynamicTest {
    @Inject
    DynamicService single;

    @Test
    public void test(){
        single.test();
    }
}