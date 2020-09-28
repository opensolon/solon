package webapp.demo2_mvc;

import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.core.XContext;
import org.noear.solon.core.XMethod;

@XMapping("/demo2/method")
@XController
public class MethodController {
    @XMapping(value = "post", method = {XMethod.POST})
    public String test_post(XContext context) {
        return context.param("name");
    }

    @XMapping(value = "put", method = {XMethod.PUT})
    public String test_put(XContext context, String name) {
        return context.param("name");
    }

    @XMapping(value = "delete", method = {XMethod.DELETE})
    public String test_delete(XContext context, String name) {
        return context.param("name");
    }

    @XMapping(value = "patch", method = {XMethod.PATCH})
    public String test_patch(XContext context, String name) {
        return context.param("name");
    }

    @XMapping(value = "post_get", method = {XMethod.POST, XMethod.GET})
    public String test_post_get(XContext context) {
        return context.path();
    }
}
