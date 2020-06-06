package feature.controller;

import feature.controller.cmds.CMD_A_0_1;
import feature.controller.cmds.CmdContext;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UApiGateway;
import org.noear.solon.extend.uapi.UApiRender;

@XController
@XMapping("/")
public class AppGateway extends UApiGateway {
    @Override
    protected void register() {
        after(new UApiRender());

        Aop.beanOnloaded(()->{
            Aop.beanForeach((name,bw)->{

            });
        });
        add(CMD_A_0_1.class);
    }

    @Override
    public XContext context(XContext ctx) {
        return new CmdContext(ctx);
    }
}
