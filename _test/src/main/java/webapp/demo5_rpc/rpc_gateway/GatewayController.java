package webapp.demo5_rpc.rpc_gateway;

import org.noear.fairy.Fairy;
import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.core.XContext;

//用普通控制器，手动实现一个网关
@XController
public class GatewayController {
    @XMapping("/demo51/{sev}/{fun**}")
    public void call(XContext context, String sev, String fun) throws Exception {

        //根据sev发现服务->完成负载->产生url
        String url = "http://localhost:8080/test/";

        String rst =
                new Fairy()
                .url(url, fun )
                .call(context.headerMap(), context.paramMap())
                .getString();

        context.output(rst);
    }
}
