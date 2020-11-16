package webapp.demo7_test;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.core.handle.Context;

@Controller
public class TestController {
    @Mapping("/demo7/test")
    public void test(Context c) throws Exception{
        c.output(c.path());
    }

    @Mapping("/demo7/exception")
    public void exception(Context c) throws Exception{
        throw new RuntimeException("出错了");
    }
}
