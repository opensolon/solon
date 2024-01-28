package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.web.cors.annotation.CrossOrigin;

@CrossOrigin
@Mapping("/demo2/cross1")
@Controller
public class Cross1Controller {
    @Mapping("/hello")
    public String hello(@Param(defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

    @Get
    @Mapping("/test")
    public void test(){

    }
}