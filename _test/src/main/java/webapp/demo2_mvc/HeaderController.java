package webapp.demo2_mvc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;

@XController
public class HeaderController {
    @XMapping("/demo2/header/")
    public String cmd(XContext ctx) throws Exception{
        return ctx.header("Water-Trace-Id");
    }
}
