package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.web.cors.annotation.CrossOrigin;

/**
 * @author noear 2022/11/24 created
 */
@Mapping("/demo2/contextpath")
@CrossOrigin(origins = "*")
@Controller
public class ContextPathController {
    @Mapping("site")
    public void site(Context ctx) {
        ctx.redirect("http://h5.noear.org");
    }

    @Mapping("furl")
    public void furl(Context ctx) {
        ctx.redirect("/demo2/mapping/a");
    }

    @Mapping("rurl")
    public void rurl(Context ctx) {
        ctx.redirect("url1");
    }

    @Mapping("url1")
    public String url1(Context ctx) {
        return ctx.path() + " :: " + ctx.pathNew();
    }
}
