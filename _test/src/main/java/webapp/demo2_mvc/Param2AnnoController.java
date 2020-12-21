package webapp.demo2_mvc;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Param;
import org.noear.solon.annotation.Singleton;
import org.noear.solon.core.handle.Context;

import java.io.IOException;

/**
 * @author noear 2020/12/20 created
 */

@Singleton(false)
@Mapping("/demo2/param2/anno")
@Controller
public class Param2AnnoController {

    @Mapping("required")
    public Object test_pm_required(@Param(required = true) String name) throws IOException {
        return name;
    }

    @Mapping("required_def")
    public Object test_pm_reqdef(@Param(required = true, defaultValue = "noear") String name) throws IOException {
        return name;
    }

    @Mapping("def")
    public Object test_pm_def(@Param(defaultValue = "noear") String name) throws IOException {
        return name;
    }

    @Mapping("name")
    public Object test_pm_name(@Param(name = "n2") String name) throws IOException {
        return name;
    }

    @Mapping("header")
    public Object test_pm_header(@Param(headerName = "X-Name") String name) throws IOException {
        return name;
    }


    //前置处理，可以在别处实施
    @Mapping(value = "attr", before = true)
    public void test_pm_attr(Context ctx) {
        ctx.attrSet("name", "noear");
        ctx.attrSet("_num", 1);
    }

    @Mapping("attr")
    public Object test_pm_attr(@Param(attrName = "name") String name, @Param(attrName = "_num") Integer num) throws IOException {
        return name + num;
    }
}
