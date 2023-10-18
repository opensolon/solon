package labs.importTest.case2;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Configuration;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2023/10/18 created
 */
@Component
public class DemoConfig {
    @Inject("${demo.name}")
    String demoName;

    public String getDemoName() {
        return demoName;
    }
}
