package feature.controller.cmds;

import org.noear.solon.annotation.XMapping;

public class CMD_A_0_1 {
    @XMapping("A.0.1")
    public Object call(CmdContext ctx){
        return  ctx.name;
    }
}
