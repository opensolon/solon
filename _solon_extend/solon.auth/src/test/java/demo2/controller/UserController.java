package demo2.controller;

import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/10/12 created
 */
@Mapping("user")
public class UserController {
    @Mapping("test")
    public String test(){
        return "ok";
    }
}
