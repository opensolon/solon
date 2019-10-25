package webapp.demo5_rpc.rpc_gateway;

import org.noear.solon.annotation.XMapping;
import org.noear.solon.annotation.XController;
import org.noear.solon.extend.wateradpter.XWaterGateway;

//用water提供的服务发现，实现的自动化网关（含负载平衡及节点自动更新）
@XMapping("/demo52/*/**")
@XController
public class GatewayWater extends XWaterGateway {
    //由water 发现服务->完成负载->产生地址->执行调用->输出结果
    public GatewayWater(){
        //这部份，也可通过water自动完成
        add("rock", "rockrpc");
        add("holdrpc","holdrpc");
    }
}
