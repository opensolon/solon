package features.masterslave;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(MasterSlaveApp.class)
public class MasterSlaveTest {
    @Inject
    MasterSlaveService service;

    @Test
    public void test(){
        service.test();
    }
}