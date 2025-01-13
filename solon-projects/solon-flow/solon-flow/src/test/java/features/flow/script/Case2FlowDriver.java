package features.flow.script;

import org.noear.solon.flow.Context;
import org.noear.solon.flow.Task;
import org.noear.solon.flow.driver.SimpleFlowDriver;

/**
 * @author noear 2025/1/11 created
 */
public class Case2FlowDriver extends SimpleFlowDriver {
    @Override
    public void handleTask(Context context, Task task) throws Throwable {
        context.result = task.node().id();
        if(task.node().id().equals("n3")) {
            context.interrupt();
            return;
        }

        super.handleTask(context, task);
    }
}
