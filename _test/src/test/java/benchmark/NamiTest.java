package benchmark;

import features.nami.Contributor;
import features.nami.GitHub;
import features.nami.Issue;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.Nami;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
public class NamiTest {
    @Test
    public void test() {
        GitHub github = Nami.builder()
                .decoder(SnackDecoder.instance)
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        github.contributors("OpenFeign", "feign");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            github.contributors("OpenFeign", "feign");
        }
        System.out.println(System.currentTimeMillis() - start);
    }

    @Test
    public void test2() {
        GitHub github = Nami.builder()
                .decoder(SnackDecoder.instance)
                .upstream(() -> "https://api.github.com")
                .create(GitHub.class);

        Issue issue = new Issue();
        issue.title = "测试";
        issue.body = "同题";

        github.createIssue(issue, "OpenFeign", "feign");

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            github.createIssue(issue, "OpenFeign", "feign");
        }
        System.out.println(System.currentTimeMillis() - start);
    }
}
