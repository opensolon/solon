package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 *
 * @author noear 2025/11/3 created
 *
 */
@Mapping("/demo2/pathprefix")
@Controller
public class PathPrefixController {
    @Mapping("hello")
    public String hello() {
        return "hello";
    }
}
