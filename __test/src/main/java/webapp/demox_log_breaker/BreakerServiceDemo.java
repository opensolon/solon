package webapp.demox_log_breaker;

//import org.noear.solon.cloud.annotation.CloudBreaker;
import org.noear.solon.proxy.annotation.ProxyComponent;

/**
 * @author noear 2021/3/14 created
 */
@ProxyComponent
public class BreakerServiceDemo {
//    @CloudBreaker("test")
    public String test() throws Exception{
        return "OK";
    }
}
