package features.core;

import org.junit.jupiter.api.Test;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;
import org.noear.solon.core.handle.Handler;
import org.noear.solon.test.SolonTest;
import webapp.App;

/**
 * @author noear 2024/9/6 created
 */
@Configuration
@SolonTest(App.class)
public class RawClzTest {
    @Inject
    AppContext appContext;

    @Test
    public void anonymousClassTest1() {
        BeanWrap beanWrap = appContext.getWrap("test_AnonymousClass_Handler");
        assert Handler.class.isAssignableFrom(beanWrap.rawClz());
    }

    @Test
    public void anonymousClassTest2() {
        BeanWrap beanWrap = appContext.getWrap("test_AnonymousClass_Handler2");
        assert Handler.class.isAssignableFrom(beanWrap.rawClz());
    }
}
