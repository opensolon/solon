package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.web.cors.annotation.CrossOrigin;

@CrossOrigin
@Socket
@Mapping("/demo2/cross3")
@Controller
public class Cross3Controller {
    @Mapping("/hello")
    public String hello(@Param(defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

    @Get
    @Mapping("/test")
    public void test(){

    }
}