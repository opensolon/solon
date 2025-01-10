package demo.solon.flow;

import org.noear.solon.flow.core.Condition;
import org.noear.solon.flow.core.Task;
import org.noear.solon.flow.core.ChainContext;

import java.util.Collection;

public class ChainContextImpl implements ChainContext {
    @Override
    public boolean isCancel() {
        return false;
    }

    @Override
    public boolean conditionHandle(Condition condition) throws Exception {
        return true;
    }

    @Override
    public void taskHandle(Collection<Task> tasks) throws Exception {
        for (Task t : tasks) {
            if ("F".equals(t.type())) {
                System.out.println(t.content());
                //RcRunner.runExpr(_context, t.content());
            } else if ("R".equals(t.type())) {
                //RcRunner.runScheme(_context, t.content());
            } else {

            }
        }
    }
}