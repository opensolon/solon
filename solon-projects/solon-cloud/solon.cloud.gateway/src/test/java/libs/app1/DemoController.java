package libs.app1;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

/**
 * @author noear 2024/8/16 created
 */
@Controller
public class DemoController {
    @Mapping("test")
    public String test(String name) {
        return name;
    }
}
