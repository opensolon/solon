package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.data.annotation.Cache;

/**
 * @author noear
 * @since 1.5
 */
@Controller
public class DemoController {
    @Cache
    public String hello(String name) {
        return String.format("Hello {0}!", name);
    }
}
