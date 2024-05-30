package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2022/12/2 created
 */
@SolonTest(App.class)
public class StaticTest extends HttpTester {
    @Test
    public void file() throws Exception {
        assert path("/file-a/a.htm").get().equals("a");
    }
}
