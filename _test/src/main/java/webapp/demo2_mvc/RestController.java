package webapp.demo2_mvc;

import org.noear.solon.annotation.*;
import org.noear.solon.core.handle.Context;
import org.noear.solon.core.handle.MethodType;

/**
 * @author noear 2021/11/25 created
 */

@Mapping("/demo2a/rest")
@Controller
public class RestController {

    @Get
    @Mapping("test")
    public String test_options(String name) {
        return "Get-" + name;
    }

    @Post
    @Mapping("test")
    public String test_post(String name) {
        return "Post-" + name;
    }

    @Put
    @Mapping("test")
    public String test_put(String name) {
        return "Put-" + name;
    }

    @Delete
    @Mapping("test")
    public String test_delete(String name) {
        return "Delete-" + name;
    }

    @Patch
    @Mapping("test")
    public String test_patch(String name) {
        return "Patch-" + name;
    }
}
