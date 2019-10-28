package webapp.demo2_mvc;

import org.noear.solon.annotation.XSingleton;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.core.XContext;

@XSingleton(false)
@XController
public class CmdController {
    @XMapping("/demo2/CMD/{cmd_name}")
    public void cmd(XContext ctx, String cmd_name) throws Exception{
        switch (cmd_name) {
            case "A.0.1": ctx.output(cmd_name);break;
            case "A.0.2": ctx.output(cmd_name);break;
            case "A.0.3": ctx.output(cmd_name);break;
        }
    }
}
