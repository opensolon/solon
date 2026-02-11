package features.solon.generic8;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;

/**
 *
 * @author noear 2026/2/11 created
 *
 */

public class MapTest {
    @Test
    public void case1() {
        AppContext appContext = new AppContext();
        appContext.beanMake(Config.class);
        appContext.start();
    }
}
