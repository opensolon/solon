package demo.solon.flow;

import org.noear.liquor.eval.Exprs;
import org.noear.solon.flow.core.*;

public class Demo1FlowDriver implements FlowDriver {
    @Override
    public boolean handleCondition(FlowContext context, Condition condition) throws Exception {
        System.out.println(condition);
        return (boolean) Exprs.eval(condition.expr(), context.model());
    }

    @Override
    public void handleTask(FlowContext context, Task task) throws Exception {
        System.out.println(task);
        Object rst = Exprs.eval(task.expr(), context.model());
        System.out.println("rst: " + rst);
    }
}