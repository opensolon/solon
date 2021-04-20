package aop;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2021/4/20 created
 */
@Component
public class DemoService {
    public DemoService(){
        System.out.println("1");
    }

    public String hello(){
        return "Hello world!";
    }
}
