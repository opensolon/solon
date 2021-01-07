package features.nami;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.nami.Nami;
import org.noear.nami.coder.snack3.SnackDecoder;
import org.noear.solon.test.SolonJUnit4ClassRunner;

import java.util.List;

/**
 * @author noear 2021/1/1 created
 */
//@RunWith(SolonJUnit4ClassRunner.class)
public class NamiTest {
//    @Test
//    public void test() {
//        GitHub github = Nami.builder()
//                .decoder(SnackDecoder.instance)
//                .upstream(() -> "https://api.github.com")
//                .create(GitHub.class);
//
//        List<Contributor> contributors = github.contributors("OpenFeign", "feign");
//        for (Contributor contributor : contributors) {
//            System.out.println(contributor.login + " (" + contributor.contributions + ")");
//        }
//
//        assert contributors.size() > 1;
//    }
//
//    @Test
//    public void test2(){
//        GitHub github = Nami.builder()
//                .decoder(SnackDecoder.instance)
//                .upstream(() -> "https://api.github.com")
//                .create(GitHub.class);
//
//        Issue issue = new Issue();
//        issue.title = "测试";
//        issue.body = "同题";
//
//
//        github.createIssue(issue,"OpenFeign", "feign");
//    }
}
