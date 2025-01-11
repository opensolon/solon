package features.flow.script;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Task;
import org.noear.solon.flow.driver.SimpleFlowDriver;

/**
 * @author noear 2025/1/11 created
 */
public class Case2FlowDriver extends SimpleFlowDriver {
    @Override
    public void handleTask(ChainContext context, Task task) throws Throwable {
        context.result = task.node().id();
        if(task.node().id().equals("n3")) {
            context.interrupt();
            return;
        }

        System.out.println(task);

        super.handleTask(context, task);
    }
}
