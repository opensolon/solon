package demo;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/5/26 created
 */
@Component
public class HelloService {
    public HelloService(){
        System.out.println("service init");
    }
}
