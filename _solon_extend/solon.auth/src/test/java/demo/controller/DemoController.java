package demo.controller;

import org.noear.solon.annotation.Mapping;

/**
 * @author noear 2022/10/12 created
 */
@Mapping("demo")
public class DemoController {
    @Mapping("test")
    public String test(){
        return "ok";
    }
}
