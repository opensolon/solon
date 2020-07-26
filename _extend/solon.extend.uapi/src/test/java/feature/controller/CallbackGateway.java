package feature.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UapiGateway;

@XController
@XMapping("/callback/**")
public class CallbackGateway extends UapiGateway {

    @Override
    protected void register() {
        addBefore(c->c.output("插一点"));

        add("wechat/payment", c -> c.output(c.path()));
        add("wechat/refund", c -> c.output(c.path()));

        addAfter(c-> c.output("加一点"));
    }
}
