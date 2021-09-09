package demo.http.controller;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

import java.util.Date;

@Controller
public class IndexController {
    @Mapping(path = "/")
    public String index() {
        return new Date().toString();
    }
}
