package demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2022/1/18 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(DemoApp.class)
public class DemoTest extends HttpTestBase {
    @Test
    public void test() throws Exception {
        path("/put").data("id", "12").data("name", "world").post();

        String json = path("/get").get();

        System.out.println(json);

        assert json.contains("world");
    }
}
