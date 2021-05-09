package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping("/demo2/method0")
@Controller
public class MethodDemoController {
    //经典写法
    @Mapping(value = "post0", method = MethodType.POST)
    public String test_post0(Context context) {
        return context.param("name");
    }

    //新增写法（新旧，2种都可以玩）
    @Post
    @Mapping("post")
    public String test_post(Context context) {
        return context.param("name");
    }

    //经典写法
    @Mapping(value = "http0", method = {MethodType.HTTP, MethodType.SOCKET})
    public String test_http0(Context context) {
        return context.param("name");
    }

    //新增写法（新旧，2种都可以玩）
    @Socket
    @Http
    @Mapping("http")
    public String test_http(Context context) {
        return context.param("name");
    }
}
