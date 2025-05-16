package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2025/5/16 created
 */
@Mapping("/demo2/redirect/")
@Controller
public class RedirectController {
    @Mapping("/jump")
    public void jump(Context ctx, int code) throws Exception {
        ctx.redirect("target", code);
    }

    @Mapping("/target")
    public String target() throws Exception {
        return "ok";
    }
}
