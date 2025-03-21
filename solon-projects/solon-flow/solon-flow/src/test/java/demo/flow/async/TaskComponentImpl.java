package demo.flow.async;

import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.TaskComponent;

public class TaskComponentImpl implements TaskComponent {

    @Override
    public void run(ChainContext context, Node node) throws Throwable {
        //做真正的任务
        System.out.println("node: " + node.id());
    }
}