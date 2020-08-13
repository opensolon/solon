package webapp.demo8_config;

import org.noear.solon.XApp;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XInject;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import webapp.models.UserModel;

@XController
public class TestController {
    @XInject("config")
    private ConfigDemo config;

    @XInject("${classpath:user.yml}")
    private UserModel user;

    @XMapping("/demo8/config_inject")
    public void test(XContext c) throws Throwable{
        c.render(config);
    }

    @XMapping("/demo8/config_all")
    public void test2(XContext c) throws Throwable{
        c.render(XApp.cfg());
    }

    @XMapping("/demo8/config_system")
    public void test3(XContext c) throws Throwable{
        c.render(System.getProperties());
    }

    @XMapping("/demo8/user")
    public void user(XContext c) throws Throwable{
        c.render(user);
    }
}
