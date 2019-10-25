package webapp.demo8_config;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;

@XController
public class TestController {
    @XInject("config")
    private ConfigDemo config;
    @XMapping("/demo8/config_inject")
    public void test(XContext c) throws Throwable{
        c.render(config);
    }
}
