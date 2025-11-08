package features.solon.bean;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.BeanWrap;

/**
 *
 * @author noear 2025/11/8 created
 *
 */
public class BeanTest {
    @Test
    public void case1() {
        AppContext appContext = new AppContext();

        appContext.beanScan(BeanTest.class);
        appContext.start();

        BeanWrap bw = appContext.getWrap(InterfaceBean.class);

        assert bw == null;
    }

    @Test
    public void case2() {
        AppContext appContext = new AppContext();

        appContext.beanMake(InterfaceBean.class);
        appContext.start();

        BeanWrap bw = appContext.getWrap(InterfaceBean.class);

        assert bw == null;
    }
}
