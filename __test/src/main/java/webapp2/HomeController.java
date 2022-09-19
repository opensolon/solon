package webapp2;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2022/9/16 created
 */
@Controller
public class HomeController {
    @Mapping("/")
    public void home(Context ctx){
        ctx.forward("/index.html");
    }
}
