package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class HeaderController {
    @Mapping("/demo2/header/")
    public String header(Context ctx) throws Exception {
        return ctx.header("Water-Trace-Id");
    }

    @Mapping("/demo2/cookie/")
    public void cookie(Context ctx) throws Exception {
        ctx.cookieSet("cookie1", "1");
        ctx.cookieSet("cookie2", "2");
    }

    @Mapping("/demo2/redirect/")
    public void redirect(Context ctx) throws Exception {
        ctx.redirect("/demo2/redirect/page");
    }

    @Mapping("/demo2/redirect/page")
    public String redirect_page(Context ctx) throws Exception {
        return "我是跳转过来的!";
    }
}
