package features.gateway.sys;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2024/10/1 created
 */
@Controller
public class AppController {
    @Mapping("/hello")
    public String hello(){
        return "hello";
    }
}
