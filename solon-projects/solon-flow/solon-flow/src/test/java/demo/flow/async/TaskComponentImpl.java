package demo.flow.async;

import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;

/**
 * @author noear 2025/3/21 created
 */
public class TaskComponentImpl extends TaskComponentPlus {
    @Override
    protected void doRun(ChainContext context, Node node, boolean async) throws Throwable {
        System.out.println("do...: " + node.id());

        if (async) {
            context.next(node);
        }
    }
}
