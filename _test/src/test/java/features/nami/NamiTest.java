package features.nami;

import org.junit.Test;
import org.noear.nami.Nami;
import org.noear.nami.decoder.SnackDecoder;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
public class NamiTest {
    @Test
    public void test() {
        GitHub github = Nami.builder()
                .decoder(SnackDecoder.instance)
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
        for (Contributor contributor : contributors) {
            System.out.println(contributor.login + " (" + contributor.contributions + ")");
        }

        assert contributors.size() > 1;
    }
}
