package demo.flow.approve;

import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.Task;
import org.noear.solon.flow.driver.*;

/**
 * @author noear 2025/1/13 created
 */
public class ApproveChainDriver extends SolonChainDriver {
    @Override
    public void handleTask(ChainContext context, Task task) throws Throwable {
        if (isChain(task.description())) {
            //如果跨链调用
            tryAsChainTask(context, task, task.description());
            return;
        }

        if (isComponent(task.description())) {
            //如果用组件运行
            tryAsComponentTask(context, task, task.description());
            return;
        }

        String instance_id = context.get("instance_id");
        String user_id = context.get("user_id");
        String role_id = context.get("role_id");


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
