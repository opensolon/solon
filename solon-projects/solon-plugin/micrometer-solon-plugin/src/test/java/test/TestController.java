package test;

import org.noear.solon.micrometer.annotation.MeterCounter;
import org.noear.solon.micrometer.annotation.MeterTimer;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;

@Controller
public class TestController {


    @Get
    @Mapping("/test")
    @MeterCounter("test")
    @MeterTimer("test")
    public String test(){
        return "test";
    }

    @Get
    @Mapping("/hello")
    @MeterCounter("hello")
    @MeterTimer("hello")
    public String hello(){
        return "hello";
    }
}
