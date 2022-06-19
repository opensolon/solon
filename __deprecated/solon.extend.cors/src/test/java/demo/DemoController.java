package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Post;

/**
 * @author noear 2022/4/28 created
 */
@Controller
public class DemoController extends BaseController{
    @Get
    @Post
    @Mapping("hello")
    public String hello(){
        return "hello";
    }
}
