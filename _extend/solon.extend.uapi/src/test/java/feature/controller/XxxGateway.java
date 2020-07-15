package feature.controller;

import feature.controller.cmds.CmdContext;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UApiGateway;
import org.noear.solon.extend.uapi.UApiRender;

@XController
@XMapping("/xxx/*")
public class XxxGateway extends UApiGateway {
    @Override
    protected void register() {
        after(UApiRender.class);

        Aop.beanOnloaded(() -> {
            Aop.beanForeach((clzName, bw) -> {
                //
                //加载所有Xxx的接口
                //
                if (clzName != null && clzName.startsWith("feature.controller.xxxs.XXX_")) {
                    add(bw);
                }
            });
        });
    }

    //替换自定义上下文
    @Override
    public XContext context(XContext ctx) {
        return new CmdContext(ctx);
    }
}
