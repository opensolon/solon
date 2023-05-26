package org.noear.solon.web.sdl.demo.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.validation.annotation.Logined;

/**
 * @author noear 2023/4/5 created
 */
@Logined //可以使用验证注解了，并且是基于sso的
@Controller
public class AdminController extends BaseController{
    @Mapping("test")
    public String test(){
        return "OK";
    }
}
