package demo;

import org.noear.solon.aspect.annotation.Service;

/**
 * @author noear 2022/5/26 created
 */
@Service
public class HelloService {
    public HelloService(){
        System.out.println("service init");
    }
}
