package feature.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.Aop;
import org.noear.solon.core.XContext;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/xxx/*")
public class XxxGateway extends UapiGateway {
    @Override
    protected void register() {

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
}
