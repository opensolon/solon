package feature.controller;

import feature.controller.cmds.*;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/cmd/*")
public class CmdGateway extends UapiGateway {
    @Override
    protected void register() {

        add(CMD_A_0_1.class);
        add(CMD_A_0_2.class);
        add(CMD_A_0_3.class);
        add(CMD_A_0_4.class);
    }

    //替换自定义上下文
    @Override
    public XContext context(XContext ctx) {
        return new CmdContext(ctx, this);
    }
}
