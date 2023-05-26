package demo.server;

import demo.protocol.HelloService;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService(group = "demo")
public class HelloServiceImpl implements HelloService {
    @Override
    public String sayHello(String name) {
        return "hello, " + name;
    }
}
