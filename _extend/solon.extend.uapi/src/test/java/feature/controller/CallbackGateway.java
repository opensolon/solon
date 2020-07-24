package feature.controller;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.extend.uapi.UApiGateway;

@XController
@XMapping("/callback/**")
public class CallbackGateway extends UApiGateway {

    @Override
    protected void register() {

        addBefore(c->c.output("插一点"));

        add("wechat/payment", c -> c.output(c.path()));
        add("wechat/refund", c -> c.output(c.path()));

        addAfter(c-> c.output("加一点"));
    }
}
