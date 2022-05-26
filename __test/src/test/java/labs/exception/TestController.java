package labs.exception;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2021/10/27 created
 */
@Controller
public class TestController {
    @Mapping("/hello2")
    public String hello2(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be empty");
        } else {
            return "Hello " + name;
        }
    }
}
