package features;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.Solon;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

import java.lang.reflect.Method;

/**
 * @author noear 2021/5/22 created
 */
@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(webapp.TestApp.class)
public class ExtractorTest {
    @Test
    public void test() {
        Method method = (Method) Solon.global().shared().get("ex_test_m");

        assert method != null;
        System.out.println("Extractor method: " + method.getName());
    }
}
