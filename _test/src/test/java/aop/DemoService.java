package aop;

import org.noear.solon.annotation.Component;
import org.noear.solon.annotation.Init;
import org.noear.solon.annotation.Inject;

/**
 * @author noear 2021/4/20 created
 */
@Component
public class DemoService {
    public DemoService(){
        System.out.println(System.currentTimeMillis());
    }

    @Inject("${xxx.user-name:}")
    String userName;

    @Init
    private void init(){
        System.out.println(System.currentTimeMillis());
    }

    public String hello(){
        return "Hello world!";
    }
}
