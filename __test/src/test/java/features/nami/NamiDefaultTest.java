package features.nami;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

/**
 * @author noear 2022/11/7 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class NamiDefaultTest {

    @NamiClient(url = "https://api.github.com")
    GitHub gitHub;

    @Test
    public void test() {
        System.out.println(gitHub.hashCode());
        assert gitHub.hashCode() > 0;

        System.out.println(gitHub.hello());
        assert "hello".equals(gitHub.hello());

        System.out.println(gitHub.toString());
        assert (GitHub.class.getName() + ".$Proxy").equals(gitHub.toString());
    }
}
