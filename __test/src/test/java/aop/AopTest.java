package aop;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;

/**
 * @author noear 2021/4/20 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class AopTest {
    @Inject
    DemoService service;

    @Test
    public void test(){
        System.out.println(service.hello());
    }
}
