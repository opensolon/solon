package demo.controller;

import demo.model.UserModel;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2022/1/18 created
 */
@Controller
public class TestController {
    @Mapping("put")
    public String put(Context ctx) {
        UserModel user = new UserModel();
        user.id = 12;
        user.name = "world";

        ctx.sessionSet("user", user);
        return "OK";
    }

    @Mapping("get")
    public UserModel get(Context ctx) {
        UserModel tmp = (UserModel)ctx.session("user");

        return tmp;
    }
}
