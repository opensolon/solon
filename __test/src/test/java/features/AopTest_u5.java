package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.demo6_aop.Bean2;

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(webapp.TestApp.class)
public class AopTest_u5 {
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
