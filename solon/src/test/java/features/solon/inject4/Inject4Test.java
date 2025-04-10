package features.solon.inject4;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;

/**
 * @author noear 2025/4/10 created
 */
public class Inject4Test {
    @Test
    public void test() {
        Exception exception = null;

        try {
            AppContext appContext = new AppContext();
            appContext.beanScan(DemoConfig.class);
            appContext.start();
        } catch (Exception e) {
            exception = e;
        }

        assert exception != null;
        exception.printStackTrace();
    }
}
