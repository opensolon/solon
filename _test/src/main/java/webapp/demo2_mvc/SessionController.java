package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Mapping(path = "/demo2/session")
@Controller
public class SessionController {
    @Mapping("id")
    public String id(Context ctx, String val) {
        return ctx.sessionId();
    }

    @Mapping("set")
    public void set(Context ctx, String val) {
        ctx.sessionSet("val", val);
    }

    @Mapping("get")
    public Object get(Context ctx) {
        return ctx.session("val");
    }


    @Mapping("token_err")
    public Object token_err(Context ctx) {
        try {
            ctx.session("val");
        } catch (Exception e) {
            e.printStackTrace();
        }

        ctx.session("val");

        return "ok";
    }
}
