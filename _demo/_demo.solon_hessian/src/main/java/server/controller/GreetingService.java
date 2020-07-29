package server.controller;

import org.noear.solon.annotation.XBean;
import server.dso.IGreetingService;

import java.io.IOException;

@XBean
public class GreetingService implements IGreetingService {
    @Override
    public String greeting(String name) throws IOException {
        return "Welcome ot the Hassian world, " + name;
    }
}
