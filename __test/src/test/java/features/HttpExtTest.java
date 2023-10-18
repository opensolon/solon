package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2023/10/10 created
 */
@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class HttpExtTest extends HttpTester {
    @Test
    public void ext_gt() throws Exception {
        String rst = path("/demo2/ext/hello").data("name","solon").post();

        assert rst != null;
        assert rst.contains("solon");
    }

    @Test
    public void ext_gt2() throws Exception {
        String rst = path("/demo2/ext/save").bodyJson("{id:1,name:'solon'}").post();

        assert rst != null;
        assert rst.contains("solon");
    }
}
