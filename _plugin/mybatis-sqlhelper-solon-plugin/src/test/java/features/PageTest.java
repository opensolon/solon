package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.snack.ONode;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.DemoApp;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class PageTest extends HttpTestBase {
    @Test
    public void test() throws Exception {
        String json = path("/page/test").get();
        assert ONode.loadStr(json).count() == 2;
    }
}
