package test1;

import org.noear.solon.annotation.Component;

/**
 * @author noear 2022/9/30 created
 */
@Component
public class TestBaseService extends TestBase {
    @Override
    public void sayHello() {
        System.out.println("hello!");
    }
}