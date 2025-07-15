package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Singleton;
import webapp.demo2_mvc.util.User;
import webapp.demo2_mvc.util.UserAnno;

/**
 * @author noear 2025/7/15 created
 */
@Singleton(false)
@Mapping("/demo2/argumentResolver")
@Controller
public class ArgumentResolverController {
    @Mapping("getUser")
    public User getUser(@UserAnno("solon") User user) {
        return user;
    }
}