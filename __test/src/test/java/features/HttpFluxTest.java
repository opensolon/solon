package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/6/19 created
 */
@SolonTest(App.class)
public class HttpFluxTest extends HttpTester {
    @Test
    public void json_map() throws Exception {
        String rst = path("/demo2/flux/").data("name","solon").post();

        assert rst != null;
        assert rst.contains("solon");
    }
}
