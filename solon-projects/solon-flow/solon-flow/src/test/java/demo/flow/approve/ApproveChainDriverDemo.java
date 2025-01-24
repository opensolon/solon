package demo.flow.approve;

import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.flow.Node;

/**
 * @author noear 2025/1/13 created
 */
public class ApproveChainDriverDemo {
    @Inject
    private FlowEngine flowEngine;

    public void demo() throws Throwable {
        //可以在链配置时指定，也可以在运行时指定
        ChainContext context = new ChainContext();

        context.put("instance_id", "i1");
        context.put("user_id", "u1");
        context.put("role_id", "r1");

        flowEngine.register("", new ApproveChainDriver());
        flowEngine.eval("c12", context);

        //运行后，获取当前展示的节点
        Node node = (Node) context.result;

        //根据转点，展示界面
    }
}
