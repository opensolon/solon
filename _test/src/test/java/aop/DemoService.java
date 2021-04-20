package aop;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;

/**
 * @author noear 2021/4/20 created
 */
@Component
public class DemoService {
    public DemoService(){
        System.out.println(System.currentTimeMillis());
    }

    @Init
    public void init(){
        System.out.println(System.currentTimeMillis());
    }

    public String hello(){
        return "Hello world!";
    }
}
