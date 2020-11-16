package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handler.Context;
import org.noear.solon.core.handler.MethodType;

@Mapping("/demo2/method")
@Controller
public class MethodController {
    @Mapping(value = "post", method = {MethodType.POST})
    public String test_post(Context context) {
        return context.param("name");
    }

    @Mapping(value = "put", method = {MethodType.PUT})
    public String test_put(Context context, String name) {
        return context.param("name");
    }

    @Mapping(value = "delete", method = {MethodType.DELETE})
    public String test_delete(Context context, String name) {
        return context.param("name");
    }

    @Mapping(value = "patch", method = {MethodType.PATCH})
    public String test_patch(Context context, String name) {
        return context.param("name");
    }

    @Mapping(value = "post_get", method = {MethodType.POST, MethodType.GET, MethodType.HEAD})
    public String test_post_get(Context context) {
        return context.path();
    }
}
