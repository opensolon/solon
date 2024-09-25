package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/9/25 created
 */
@Mapping("/demo2/sequence")
@Controller
public class SequenceController {
    @Mapping("header")
    public String header(Context ctx) {
        return ctx.header("v1") + ";" + ctx.headerMap().holder("v1").getLastValue();
    }

    @Mapping("cookie")
    public String cookie(Context ctx) {
        return ctx.cookie("v1") + ";" + ctx.cookieMap().holder("v1").getLastValue();
    }

    @Mapping("param")
    public String param(Context ctx) {
        return ctx.param("v1") + ";" + ctx.paramMap().holder("v1").getLastValue();
    }
}
