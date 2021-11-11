package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class TestController {
    @Mapping("/")
    public String hello() {
        return "hello solon world";
    }
}
