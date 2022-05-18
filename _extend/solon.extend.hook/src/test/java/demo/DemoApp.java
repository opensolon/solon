package demo;

import org.noear.solon.Solon;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.hook.annotation.HookBefore;

/**
 * @author noear
 * @since 1.8
 */
@Controller
public class DemoApp {
    public static void main(String[] args) {
        Solon.start(DemoApp.class, args);
    }

    @HookBefore("hello")
    @Mapping("hello")
    public String hello(String name) {
        return "Hello " + name;
    }
}
