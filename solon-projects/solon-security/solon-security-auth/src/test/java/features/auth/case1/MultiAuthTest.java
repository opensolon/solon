package features.auth.case1;

import org.junit.jupiter.api.Test;
import org.noear.solon.test.HttpTester;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2024/10/16 created
 */
@SolonTest(DemoApp.class)
public class MultiAuthTest extends HttpTester {
    @Test
    public void admin() throws Exception {
        assert "你的IP不在白名单".equals(path("/admin/login").get());
        assert "你的IP不在白名单".equals(path("/admin/test").get());
    }

    @Test
    public void user() throws Exception {
        assert "user/login".equals(path("/user/login").get());
        assert "user/login".equals(path("/user/test").get());
    }

    @Test
    public void user_path() throws Exception {
        assert path("/user/login_path").head() == 403;
    }
}
