package webapp.controller;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Mapping;

@Component
public class HelloService {
    @Mapping("hello")
    public String hello(String name){
        return "hello world!";
    }
}
