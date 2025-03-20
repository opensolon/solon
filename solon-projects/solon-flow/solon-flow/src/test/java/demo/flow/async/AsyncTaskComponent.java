package demo.flow.async;

import org.noear.solon.Utils;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.TaskComponent;

/**
 * @author noear 2025/3/20 created
 */
public class AsyncTaskComponent implements TaskComponent {
    @Override
    public void run(ChainContext context, Node node) throws Throwable {
        //中断当前分支
        context.interrupt();

        //异步执行
        Utils.async(() -> {
            //做真正的任务
            System.out.println("node: " + node.id());
            //向后驱动
            next(context, node);
        });
    }

    private void next(ChainContext context, Node node) {
        try {
            context.engine().next(node, context);
        } catch (Throwable ex) {
            ex.printStackTrace();
            context.stop();
        }
    }
}