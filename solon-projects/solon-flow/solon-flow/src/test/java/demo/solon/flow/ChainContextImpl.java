package demo.solon.flow;

import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Task;
import org.noear.solon.flow.core.ChainContext;

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

            switch (t.type()) {
                case function: {
                    System.out.println(t.content());
                    //RcRunner.runExpr(_context, t.content());
                    break;
                }
                case rule: {
                    //RcRunner.runScheme(_context, t.content());
                }
                break;
                default: {

                }
                break;
            }
        }
    }
}