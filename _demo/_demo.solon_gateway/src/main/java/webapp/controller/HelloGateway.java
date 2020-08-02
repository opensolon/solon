package webapp.controller;

import org.noear.solon.XGateway;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;

@XMapping("/")
@XController
public class HelloGateway extends XGateway {
    @Override
    protected void register() {
        add(HelloService.class);
    }
}
