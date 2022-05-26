package org.noear.solon.extend.consul.test;

import org.noear.nami.annotation.NamiClient;
import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class HelloController {

    @NamiClient(name = "test-consul-api")
    HelloInterface helloInterface;

    @Mapping("/hello")
    public String sayHello() {
        return "config:" + Solon.cfg().get("hello")+",rpc:"+helloInterface.hello0();
    }

    @Mapping("/hello0")
    public String sayHello0() {
        return "hello0";
    }
}
