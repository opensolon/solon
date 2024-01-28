package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Socket;
import org.noear.solon.core.handle.MethodType;
import org.noear.solon.web.cors.annotation.CrossOrigin;

@CrossOrigin
@Mapping("/demo2/cross22")
@Controller
public class Cross22Controller {
    @Socket
    @Mapping("/hello")
    public String hello(@Param(defaultValue = "world") String name) {
        return String.format("Hello %s!", name);
    }

    @Mapping(value = "/test", method = MethodType.GET)
    public void test() {

    }
}