package server.controller;

import org.noear.solon.annotation.XBean;
import org.noear.solon.annotation.XMapping;
import server.dso.IGreetingService;

import java.io.IOException;

@XMapping("/GreetingService/")
@XBean(remoting = true)
public class GreetingService implements IGreetingService {
    @Override
    public String greeting(String name) throws IOException {
        return "Welcome ot the Hassian world, " + name;
    }
}
