package test1;

import org.noear.solon.aspect.annotation.Service;

/**
 * @author noear 2022/9/30 created
 */
@Service
public class TestBaseService extends TestBase {
    @Override
    public void sayHello() {
        System.out.println("hello!");
    }
}