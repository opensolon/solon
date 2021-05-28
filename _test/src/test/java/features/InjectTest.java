package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2021/5/28 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class InjectTest {
    @Inject(value = "${username}", autoRefreshed = true)
    String username;

    @Test
    public void xxx() {
        assert "noear".equals(username);

        Solon.cfg().setProperty("username", "xxx");

        assert "xxx".equals(username);
    }
}
