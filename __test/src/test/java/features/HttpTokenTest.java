package features;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

import java.io.IOException;

/**
 * @author noear 2021/6/4 created
 */
@SolonTest(App.class)
public class HttpTokenTest extends HttpTester {

    @Test
    public void token_err() throws IOException {
        assert path("/demo2/session/token_err")
                .header("Set-Cookie","Token=xx.xx.xxxxxxxx")
                .get().equals("ok");
    }
}
