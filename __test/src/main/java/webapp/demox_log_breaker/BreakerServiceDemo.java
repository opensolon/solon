package webapp.demox_log_breaker;

//import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.annotation.Component;

/**
 * @author noear 2021/3/14 created
 */
@Component
public class BreakerServiceDemo {
//    @CloudBreaker("test")
    public String test() throws Exception{
        return "OK";
    }
}
