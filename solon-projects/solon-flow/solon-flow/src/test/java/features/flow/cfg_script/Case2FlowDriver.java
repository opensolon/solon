package features.flow.cfg_script;

import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Task;
import org.noear.solon.flow.driver.SolonChainDriver;

/**
 * @author noear 2025/1/11 created
 */
public class Case2FlowDriver extends SolonChainDriver {
    @Override
    public void handleTask(ChainContext context, Task task) throws Throwable {
        context.result = task.node().id();
        if(task.node().id().equals("n-3")) {
            context.interrupt();
            return;
        }

        super.handleTask(context, task);
    }
}
