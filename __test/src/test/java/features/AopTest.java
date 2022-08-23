package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.demo6_aop.Bean2;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
//@SolonTest(value = webapp.TestApp.class, args = "-server.port=9001")
public class AopTest {
    @Inject
    Bean2 bean2;

    @Inject("${testpath}")
    String testpath;

    //双向依赖的bean测试
    //
    @Test
    public void test() {
        assert "bean2bean1".equals(bean2.namePlus());
        assert "D:/abc-1-2.12/xx.xml".equals(testpath);
        System.out.println(testpath);
    }
}
