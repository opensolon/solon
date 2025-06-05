package features.yaml;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;

/**
 * @author noear 2025/6/5 created
 */
public class PropDefTest {
    @Test
    public void testPropDef() throws Throwable {
        SimpleSolonApp app = new SimpleSolonApp(PropDefTest.class, new String[]{"-cfg=app-def-test.yml"});
        app.start(x->{
            x.enableScanning(false);
        });


       String server = app.cfg().get("solon.config.nacos.server");
       System.out.println(server);
    }
}
