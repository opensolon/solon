package features.data;

import libs.DataSourceTmp;
import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.Solon;

/**
 * @author noear 2024/8/21 created
 */
public class DsTest {
    @Test
    public void case1_cfg() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(DsTest.class, new String[]{"--cfg=test-ds.yml"});
        app.start(null);

        DataSourceTmp tmp = Solon.context().getBean("db1");
        assert tmp != null;
        assert tmp.getProps().size() > 2;
        assert "root".equals(tmp.getProps().get("username"));
    }
}
