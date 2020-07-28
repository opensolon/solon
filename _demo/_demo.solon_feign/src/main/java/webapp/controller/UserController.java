package webapp.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import webapp.model.User;

@XMapping("users")
@XController
public class UserController {
    @XMapping(value = "get1")
    public String get1(String name) {
        return name.toUpperCase();
    }

    @XMapping(value = "get2")
    public void get2(String name, XContext ctx) throws Throwable{
        User user = new User();

        user.setName(name);
        user.setSex(1);
        user.setIcon("fa-btn");

        ctx.remotingSet(true);
        ctx.render(user);
        ctx.remotingSet(false);
    }
}