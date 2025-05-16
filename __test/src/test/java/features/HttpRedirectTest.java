package features;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2025/5/16 created
 */
@SolonTest(App.class)
public class HttpRedirectTest extends HttpTester {
    @Test
    public void case301() throws Exception {
        assert path("/demo2/redirect/jump?code=301").get().equals("ok");
    }

    @Test
    public void case302() throws Exception {
        assert path("/demo2/redirect/jump?code=302").get().equals("ok");
    }

    @Test
    public void case307() throws Exception {
        assert path("/demo2/redirect/jump?code=307").get().equals("ok");
    }
}
