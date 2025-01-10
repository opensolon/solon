package demo.solon.flow;

import org.noear.solon.flow.Condition;
import org.noear.solon.flow.Task;
import org.noear.solon.flow.TaskType;
import org.noear.solon.flow.ChainContext;

import java.util.List;

public class ChainContextImpl implements ChainContext {
    @Override
    public boolean is_cancel() {
        return false;
    }

    @Override
    public boolean condition_handle(Condition condition) throws Exception {
        return true;
    }

    @Override
    public void task_handle(List<Task> tasks) throws Exception {
        for (Task t : tasks) {
            if (t.type() == TaskType.function) {
                System.out.println(t.content());
                //RcRunner.runExpr(_context, t.content());
            }

            if (t.type() == TaskType.rule) {
                //RcRunner.runScheme(_context, t.content());
            }
        }
    }
}
