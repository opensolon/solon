package demo2.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/10/12 created
 */
@Controller
public class HomeController {
    @Mapping("/")
    public String home(){
        return "ok";
    }

    @Mapping("/test")
    public String test(){
        return "ok";
    }
}
