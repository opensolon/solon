package webapp.demo5_rpc;

import org.noear.solon.annotation.Component;
import org.noear.solon.core.LoadBalance;

/**
 * 定义一个负载器（可以对接发现服务）
 * */
@Component("local")
public class UserUpstream implements LoadBalance {
    @Override
    public String getServer() {
        return "http://localhost:8080";
    }
}
