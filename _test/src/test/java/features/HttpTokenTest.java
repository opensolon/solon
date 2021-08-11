package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.test.HttpTestBase;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.io.IOException;

/**
 * @author noear 2021/6/4 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class HttpTokenTest extends HttpTestBase {

    @Test
    public void token_err() throws IOException {
        assert path("/demo2/session/token_err")
                .header("Set-Cookie","Token=xx.xx.xxxxxxxx")
                .get().equals("ok");
    }
}
