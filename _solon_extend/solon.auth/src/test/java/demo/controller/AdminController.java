package demo.controller;

import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/10/12 created
 */
@Mapping("admin")
public class AdminController {
    @Mapping("test")
    public String test(){
        return "ok";
    }
}
