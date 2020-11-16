package webapp.controller;

import org.noear.solon.core.handler.Gateway;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Mapping("/*")
@Controller
public class HelloGateway extends Gateway {
    @Override
    protected void register() {
        add(HelloService.class);
    }
}
