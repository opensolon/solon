package org.noear.solon.opentracing.demo.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2021/6/7 created
 */
@Controller
public class TestController {
    @Mapping("/")
    public String hello(String name) {
        return "Hello " + name;
    }
}
