package org.noear.solon.opentracing.demo.controller;

import org.noear.nami.annotation.NamiClient;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.opentracing.demo.HelloService;

/**
 * @author noear 2021/6/7 created
 */
@Controller
public class TestController {
    @NamiClient
    HelloService helloService;

    @Mapping("/")
    public String hello(String name) {
        return "Rpc: " + helloService.hello(name);
    }
}
