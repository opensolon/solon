package webapp.controller;

import org.noear.solon.annotation.Bean;
import org.noear.solon.annotation.Mapping;

@Bean
public class HelloService {
    @Mapping("hello")
    public String hello(String name){
        return "hello world!";
    }
}
