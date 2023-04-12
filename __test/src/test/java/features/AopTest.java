package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.demo6_aop.Bean2;
import webapp.dso.AutoConfigTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
//@SolonTest(value = webapp.TestApp.class, args = "-server.port=9001")
public class AopTest {
    @Inject
    Bean2 bean2;

    @Inject("${testpath}")
    String testpath;

    @Inject
    AutoConfigTest autoConfigTest;

    //双向依赖的bean测试
    //
    @Test
    public void test() {
        assert "bean2bean1".equals(bean2.namePlus());
        assert "D:/abc-1-2.12/xx.xml".equals(testpath);
        System.out.println(testpath);
    }

    @Test
    public void test1() {
        assert "a".equals(autoConfigTest.getUsername());
        Solon.cfg().setProperty("autorefresh.username", "b");
    }

    @Test
    public void test2() {
        assert "b".equals(autoConfigTest.getUsername());
    }
}
