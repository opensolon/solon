package test1;

import org.noear.solon.annotation.ProxyComponent;

/**
 * @author noear 2022/9/30 created
 */
@ProxyComponent
public class TestBaseService extends TestBase {
    @Override
    public void sayHello() {
        System.out.println("hello!");
    }
}