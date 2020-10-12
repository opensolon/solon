package net.hasor.solon.boot.test;
import net.hasor.core.AppContext;
import net.hasor.core.Environment;
import net.hasor.core.Settings;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.noear.solon.annotation.XInject;
import org.noear.solon.test.SolonJUnit4ClassRunner;
import org.noear.solon.test.SolonTest;

@RunWith(SolonJUnit4ClassRunner.class)
@SolonTest(BootShareHasor_1.class)
public class BootShareHasor_1_Test {
    @XInject
    private AppContext appContext;

    @Test
    public void contextLoads() {
        //
        Environment environment = appContext.getEnvironment();
        Settings settings = environment.getSettings();
        //
        assert "HelloWord".equals(environment.getVariable("msg"));
        assert "HelloWord".equals(settings.getString("msg"));
    }
}
