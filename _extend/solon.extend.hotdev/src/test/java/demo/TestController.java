package demo;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

import java.util.Date;

@Controller
public class TestController {
    static Date date; //静态变量，也会重构

    @Mapping("/")
    public String hello() {
        if (date == null) {
            date = new Date();
        }

        return "hello world1, " + date.getTime();
    }
}
