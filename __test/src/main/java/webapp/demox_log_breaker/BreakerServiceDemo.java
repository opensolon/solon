package webapp.demox_log_breaker;

//import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.extend.aspect.annotation.Service;

/**
 * @author noear 2021/3/14 created
 */
@Service
public class BreakerServiceDemo {
//    @CloudBreaker("test")
    public String test() throws Exception{
        return "OK";
    }
}
