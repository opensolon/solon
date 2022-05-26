package webapp.demo8_config;

import org.noear.solon.Solon;
import org.noear.solon.SolonApp;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import webapp.models.UserModel;

@Controller
public class TestController {
    @Inject
    private ConfigDemo config;

    @Inject("${classpath:user.yml}")
    private UserModel user;

    @Mapping("/demo8/config_inject")
    public void test(Context c) throws Throwable{
        c.render(config);
    }

    @Mapping("/demo8/config_all")
    public void test2(Context c) throws Throwable{
        c.render(Solon.cfg());
    }

    @Mapping("/demo8/config_system")
    public void test3(Context c) throws Throwable{
        c.render(System.getProperties());
    }

    @Mapping("/demo8/user")
    public void user(Context c) throws Throwable{
        c.render(user);
    }
}
