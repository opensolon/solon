package features.nami;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.nami.annotation.NamiClient;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2022/11/7 created
 */
@SolonTest(App.class)
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
