package feature.controller.cmds;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UapiCode;

public class CMD_A_0_2 {
    @XMapping("A.0.2")
    public Object call(CmdContext ctx){
        throw new UapiCode(12);
    }

    @XMapping("A.0.22")
    public Object call2(CmdContext ctx){
        throw new UapiCode(12,"参数@name没有输");
    }
}
