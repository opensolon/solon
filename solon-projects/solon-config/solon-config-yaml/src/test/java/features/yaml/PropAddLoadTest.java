package features.yaml;


import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.Solon;
import org.noear.solon.core.AppContext;
import org.noear.solon.core.Plugin;

import java.util.Properties;

/**
 * @author noear 2025/5/29 created
 */
public class PropAddLoadTest {
    @Test
    public void case1() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(PropAddLoadTest.class,
                "-cfg=app-cfg-test.yml",
                "-testing=1");
        app.start(x -> {
            x.enableScanning(false);
            x.pluginAdd(990, new Plugin() {
                @Override
                public void start(AppContext context) throws Throwable {
                    Properties properties = new Properties();
                    properties.put("demo2.datasource.driverClassName", "456");
                    Solon.cfg().loadAdd(properties);
                }
            });
        });

        Properties properties = app.cfg().getProp("app2.db");
        System.out.println(properties);

        assert "{driverClassName=123, driverClassName2=456, driverClassName1=456}".equals(properties.toString());
    }
}
