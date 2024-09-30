package features.core;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.test.SolonTest;
import webapp.App;
import webapp.dso.GenericsTestConfig;

/**
 * @author noear 2024/9/30 created
 */
@Configuration
@SolonTest(App.class)
public class GenericsTest {
    @Inject
    GenericsTestConfig genericsTestConfig;

    @Test
    public void test1() {
        assert genericsTestConfig.wxCallbackContext != null;

        genericsTestConfig.wxCallbackContext.check();
        genericsTestConfig.fsCallbackContext.check();
    }

    @Test
    public void test1_static() {
        assert GenericsTestConfig.fsCallbackContext != null;

        GenericsTestConfig.fsCallbackContext.check();
        GenericsTestConfig.fsCallbackContext.check();
    }

    @Test
    public void test2() {
        GenericsTestConfig.TestConfig.check();
    }
}
