package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/6/19 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(App.class)
public class HttpFluxTest extends HttpTester {
    @Test
    public void json_map() throws Exception {
        String rst = path("/demo2/flux/").data("name","solon").post();

        assert rst != null;
        assert rst.contains("solon");
    }
}
