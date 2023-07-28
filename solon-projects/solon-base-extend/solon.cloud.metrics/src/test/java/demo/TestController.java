package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Get;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.cloud.metrics.annotation.MeterCounter;
import org.noear.solon.cloud.metrics.annotation.MeterTimer;

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
