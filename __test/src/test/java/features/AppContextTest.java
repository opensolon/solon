package features;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.noear.solon.core.AppClassLoader;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Props;
import org.noear.solon.test.SolonJUnit5Extension;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/4/10 created
 */

@ExtendWith(SolonJUnit5Extension.class)
@SolonTest(App.class)
public class AppContextTest {
    @Test
    public void put_del() {
        AppContext context = new AppContext(new AppClassLoader(), new Props());
        context.putWrap("test", context.wrapAndPut(String.class, "test"));
        assert context.getBean("test") != null;

        context.delWrap("test");
        assert context.getBean("test") == null;
    }
}
