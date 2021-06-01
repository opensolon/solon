package webapp.demox_log_breaker;

import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.cloud.annotation.CloudBreaker;

/**
 * @author noear 2021/3/14 created
 */
@Controller
public class BreakerDemo {
    @CloudBreaker("test")
    @Mapping("/demox/test")
    public String test() throws Exception{
        return "OK";
    }
}
