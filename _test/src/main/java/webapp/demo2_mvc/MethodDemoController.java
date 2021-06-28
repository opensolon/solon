package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.MethodType;

@Mapping("/demo2/method0")
@Controller
public class MethodDemoController {
    /**
     * (1)没事儿，这么写就够了
     */

    //经典写法
    @Mapping("demo00")
    public String demo00(String name) {
        return name;
    }

    /**
     * (2)像 REST api，强调 method的
     */

    //经典写法
    @Mapping(path = "demo10", method = MethodType.POST)
    public String demo10(String name) {
        return name;
    }

    //新增写法（新旧，2种都可以玩）
    @Post
    @Mapping("demo11")
    public String demo11(String name) {
        return name;
    }

    //经典写法
    @Mapping(value = "demo20", method = {MethodType.HTTP, MethodType.SOCKET})
    public String demo20(String name) {
        return name;
    }

    //新增写法（新旧，2种都可以玩）
    @Socket
    @Http
    @Mapping("demo21")
    public String demo21(String name) {
        return name;
    }
}
