package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demo6_aop.Bean2;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class AopTest {
    @Inject
    Bean2 bean2;

    //双向依赖的bean测试
    //
    @Test
    public void test(){
        assert  "bean2bean1".equals(bean2.namePlus());
    }
}
