package webapp.demoa_interceptor;

import org.noear.solon.annotation.XAround;
import org.noear.solon.annotation.XBefore;
import org.noear.solon.annotation.XController;
import org.noear.solon.annotation.XMapping;

@XBefore({BeforeHandler.class})
@XController
public class TestController {
    @XAround(AroundHandler.class)
    @XMapping("/demoa/test/")
    public void test(){

    }
}
