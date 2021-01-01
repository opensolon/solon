package features;

import features.rpc.Contributor;
import features.rpc.GitHub;
import org.junit.Test;
import org.noear.nami.Nami;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public class NamiTest {
    @Test
    public void test() {
        GitHub github = Nami.builder()
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }
    }
}
