package org.noear.solon.opentracing.demo.controller;

import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Remoting;
import org.noear.solon.opentracing.demo.HelloService;

/**
 * @author noear 2021/6/7 created
 */
@Mapping("rpc")
@Remoting
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String name) {
        return "Hello " + name;
    }
}
