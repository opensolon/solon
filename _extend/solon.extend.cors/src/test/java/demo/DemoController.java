package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.extend.cors.annotation.CrossOrigin;

/**
 * @author noear 2022/4/28 created
 */
@Controller
public class DemoController extends BaseController{
    @Mapping("hello")
    public String hello(){
        return "hello";
    }
}
