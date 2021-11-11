package org.noear.solon.demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Controller
@Mapping("test")
public class TestController {
    @Mapping("sayhello")
    public String sayhello(){
        return "hello solon world1";
    }
}
