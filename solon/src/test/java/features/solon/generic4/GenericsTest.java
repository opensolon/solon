package features.solon.generic4;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;

/**
 * @author noear 2025/3/19 created
 */
public class GenericsTest {
    @Test
    public void test() {
        AppContext appContext = new AppContext();

        appContext.beanScan(BeanSearcherConvertors.class);
        appContext.start();

        BeanReflector beanReflector = appContext.getBean(BeanReflector.class);
        ResultFilter resultFilter = appContext.getBean(ResultFilter.class);

        int size = beanReflector.getConvertors().size();
        System.out.println(size);
        assert size == 14;

        size = resultFilter.getLabelLoaders().size();
        System.out.println(size);
        assert size == 1;
    }
}