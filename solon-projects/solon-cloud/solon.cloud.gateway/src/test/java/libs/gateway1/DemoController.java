package libs.gateway1;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
public class DemoController {
    @Mapping("hello")
    public String hello(String name) {
        return name;
    }
}
