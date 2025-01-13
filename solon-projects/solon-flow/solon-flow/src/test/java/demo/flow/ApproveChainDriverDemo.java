package demo.flow;

import org.noear.snack.ONode;
import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.FlowEngine;

/**
 * @author noear 2025/1/13 created
 */
public class ApproveChainDriverDemo {
    @Inject
    private FlowEngine flowEngine;

    public void demo() throws Throwable {
        //可以在链配置时指定，也可以在运行时指定
        ChainContext context = new ChainContext(new ApproveChainDriver());

        context.paramSet("instance_id", "123");
        context.paramSet("user_id", "123");
        context.paramSet("role_id", "123");

        flowEngine.eval("c12", context);

        ONode oNode = (ONode) context.result;
    }
}
