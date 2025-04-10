package features.solon.inject2;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.AppContext;

/**
 * @author noear 2025/3/19 created
 */
public class Inject2Test {
    @Test
    public void case1() {
        AppContext appContext = new AppContext();
        appContext.beanScan(Config.class);
        appContext.start();

        Config config = appContext.getBean(Config.class);

        assert config.dsBeanList != null;
        assert config.dsBeanList.size() == 2;

        assert config.dnBeanMap != null;
        assert config.dnBeanMap.size() == 2;
        assert config.dnBeanMap.get("DnBean2") != null;
    }
}
