package demo.flow;

import org.noear.solon.flow.Condition;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.Task;
import org.noear.solon.flow.driver.*;

/**
 * @author noear 2025/1/13 created
 */
public class ApproveChainDriver extends SimpleChainDriver {
    @Override
    public void onNodeStart(ChainContext context, Node node) {

    }

    @Override
    public void onNodeEnd(ChainContext context, Node node) {

    }

    @Override
    public boolean handleCondition(ChainContext context, Condition condition) throws Throwable {
        return super.handleCondition(context, condition);
    }

    @Override
    public void handleTask(ChainContext context, Task task) throws Throwable {
        if (tryIfChainTask(context, task, task.description())) {
            //如果跨链调用
            return;
        }

        if (tryIfComponentTask(context, task, task.description())) {
            //如果用组件运行
            return;
        }

        String instance_id = context.param("instance_id");
        String user_id = context.param("user_id");
        String role_id = context.param("role_id");


        String chain_id = task.node().chain().id();
        String task_id = task.node().id();

        //把状态批量加载到上下文参考（或者通过数据库查找状态）
        TaskState taskState = null;//查询任务装态

        if (taskState == null) {
            //中断（流，不会再往下驱动），等用户操作出状态
            context.interrupt();

            //查询数据库，是否有提醒记录。如果没有，发布通知
            //...

            //如果当前用户匹配这个节点任务
            if(role_id.equals(task.node().meta().get("role_id"))){
                //则把这个节点，作为结果（用于展示界面）
                context.result = task.node();
            }
        }
    }
}
