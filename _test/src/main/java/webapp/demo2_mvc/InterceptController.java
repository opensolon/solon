package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2020/12/14 created
 */
@Controller
public class InterceptController {
    @Mapping(path = "/demo2/intercept/**",before = true)
    public void bef(Context ctx) throws Exception{
        ctx.output("intercept");
    }

    @Mapping("/demo2/intercept/demo")
    public String demo() throws Exception{
        return "demo";
    }
}
