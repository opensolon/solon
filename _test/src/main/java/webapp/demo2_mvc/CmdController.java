package webapp.demo2_mvc;

import org.noear.solon.annotation.Inject;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.handle.Context;

@Singleton(false)
@Controller
public class CmdController {

    @Inject
    CmdService cmdService;

    @Mapping("/demo2/CMD/{cmd_name}")
    public void cmd(Context ctx, String cmd_name) throws Exception{
        switch (cmd_name) {
            case "A.0.1": ctx.output(cmdService.name(cmd_name));break;
            case "A.0.2": ctx.output(cmdService.name(cmd_name));break;
            case "A.0.3": ctx.output(cmdService.name(cmd_name));break;
            default:ctx.statusSet(404);break;
        }
    }
}
