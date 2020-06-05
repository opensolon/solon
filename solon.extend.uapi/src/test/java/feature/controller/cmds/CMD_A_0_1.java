package feature.controller.cmds;

import org.noear.solon.extend.uapi.UApi;

public class CMD_A_0_1 extends UApi {
    @Override
    public String name() {
        return  "A.0.1";
    }

    public Object call(CmdContext ctx){
        return  ctx.name;
    }
}
