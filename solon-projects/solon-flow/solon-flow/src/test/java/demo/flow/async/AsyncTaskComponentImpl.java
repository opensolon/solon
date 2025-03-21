package demo.flow.async;

import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;

/**
 * @author noear 2025/3/21 created
 */
public class AsyncTaskComponentImpl extends AsyncTaskComponent {
    @Override
    protected void asyncRun(ChainContext context, Node node) throws Throwable {
        System.out.println("do...: " + node.id());
        context.next(node);
    }
}
