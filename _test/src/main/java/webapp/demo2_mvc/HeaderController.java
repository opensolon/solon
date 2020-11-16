package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class HeaderController {
    @Mapping("/demo2/header/")
    public String cmd(Context ctx) throws Exception{
        return ctx.header("Water-Trace-Id");
    }
}
