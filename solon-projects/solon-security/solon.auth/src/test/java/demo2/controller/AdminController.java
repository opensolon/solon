package demo2.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/10/12 created
 */
@Mapping("admin")
@Controller
public class AdminController {
    @Mapping("login")
    public String login(){
        return "ok";
    }
    @Mapping("test")
    public String test(){
        return "ok";
    }
}
