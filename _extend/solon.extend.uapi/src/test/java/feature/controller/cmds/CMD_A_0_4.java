package feature.controller.cmds;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UapiCode;

public class CMD_A_0_4 {
    @XMapping("A.0.4")
    public Object call(CmdContext ctx){
        throw new UapiCode(new RuntimeException("没饭了"));
    }
}
