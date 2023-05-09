package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2022/12/2 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class StaticTest extends HttpTester {
    @Test
    public void file() throws Exception {
        assert path("/file-a/a.htm").get().equals("a");
    }
}
