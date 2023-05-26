package demo;

import org.noear.solon.annotation.ProxyComponent;

/**
 * @author noear 2022/5/26 created
 */
@ProxyComponent
public class HelloService {
    public HelloService(){
        System.out.println("service init");
    }
}
