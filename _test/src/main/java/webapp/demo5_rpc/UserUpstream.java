package webapp.demo5_rpc;

import org.noear.solon.annotation.XBean;
import org.noear.solon.core.XUpstream;

/**
 * 定义一个负载器（可以对接发现服务）
 * */
@XBean("local")
public class UserUpstream implements XUpstream {
    @Override
    public String getServer() {
        return "http://localhost:8080";
    }
}
