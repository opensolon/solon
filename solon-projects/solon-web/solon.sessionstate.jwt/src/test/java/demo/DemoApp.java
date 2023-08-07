package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

import java.util.Date;

/**
 * @author noear 2022/10/26 created
 */
@Controller
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }

    @Mapping("get")
    public Object get(Context ctx) {
        return ctx.session("user");
    }


    @Mapping("set")
    public void set(Context ctx) {
        User user = new User();
        user.id = 1;
        user.name = "wold";
        ctx.sessionSet("user", user);
        ctx.sessionSet("int", 1);
        ctx.sessionSet("long", 1L);
        ctx.sessionSet("str", "test");
        ctx.sessionSet("time", new Date());
        ctx.sessionSet("bool", true);
        ctx.sessionSet("flot", 0.12);
    }
}
