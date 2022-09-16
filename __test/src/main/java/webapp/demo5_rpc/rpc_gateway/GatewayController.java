package webapp.demo5_rpc.rpc_gateway;

import org.noear.nami.Nami;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.annotation.Controller;
import org.noear.solon.core.handle.Context;

//用普通控制器，手动实现一个网关
@Controller
public class GatewayController {
    @Mapping("/demo51/{sev}/{fun**}")
    public void call(Context context, String sev, String fun) throws Exception {

        //根据sev发现服务->完成负载->产生url
        String url = "http://localhost:8080/test/";

        String rst = new Nami()
                .url(url, fun)
                .call(context.headerMap(), context.paramMap())
                .getString();

        context.output(rst);
    }
}
