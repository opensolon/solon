package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

@Mapping("/demo2/method")
@Controller
public class MethodController {
    @Mapping(path = "post", method = MethodType.POST)
    public String test_post(Context context) {
        return context.param("name");
    }

    @Mapping(path = "put", method =MethodType.PUT)
    public String test_put(Context context, String name) {
        return context.param("name");
    }

    @Mapping(path = "delete", method = {MethodType.DELETE})
    public String test_delete(Context context, String name) {
        return context.param("name");
    }

    @Mapping(path = "patch", method = {MethodType.PATCH})
    public String test_patch(Context context, String name) {
        return context.param("name");
    }

    @Mapping(value = "options",method = MethodType.OPTIONS)
    public String test_options(Context context, String name) {
        return context.param("name");
    }

    @Mapping(path = "post_get", method = {MethodType.POST, MethodType.GET, MethodType.HEAD})
    public String test_post_get(Context context) {
        return context.path();
    }
}
