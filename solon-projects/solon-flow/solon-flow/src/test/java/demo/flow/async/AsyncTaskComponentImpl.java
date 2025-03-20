package demo.flow.async;

import org.noear.solon.flow.AsyncTaskComponent;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;

public class AsyncTaskComponentImpl extends AsyncTaskComponent {
    @Override
    protected void asyncRun(ChainContext context, Node node) throws Throwable {
        //做真正的任务
        System.out.println("node: " + node.id());
    }
}