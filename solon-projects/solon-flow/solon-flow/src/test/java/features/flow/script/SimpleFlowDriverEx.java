package features.flow.script;

import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.core.Task;
import org.noear.solon.flow.driver.SimpleFlowDriver;

/**
 * @author noear 2025/1/11 created
 */
public class SimpleFlowDriverEx extends SimpleFlowDriver {
    @Override
    public void handleTask(ChainContext context, Task task) throws Throwable {
        System.out.println("task: "  + task);
        super.handleTask(context, task);
    }
}
