package webapp.demoa_interceptor;

import org.noear.solon.annotation.Around;
import org.noear.solon.annotation.Before;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;

@Before({BeforeHandler.class})
@Controller
public class TestController {
    @Around(AroundHandler.class)
    @Mapping("/demoa/test/")
    public void test(){

    }
}
