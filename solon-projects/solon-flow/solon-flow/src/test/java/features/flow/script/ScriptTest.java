package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.SimpleFlowDriver;

/**
 * @author noear 2025/1/10 created
 */
public class ScriptTest {
    @Test
    public void case1() throws Throwable {
        Chain chain = new Chain("c1", "c1", new SimpleFlowDriver() {
            @Override
            public void handleTask(ChainContext context, Task task) throws Throwable {
                System.out.println(task.node());
                super.handleTask(context, task);
            }
        });

        chain.addNode("n1", "n1", ElementType.start);
        chain.addNode("n2", "n2", ElementType.execute, null, "context.result=111 + a;");
        chain.addNode("n3", "n3", ElementType.execute, null, "context.result=222 + b;");
        chain.addNode("n4", "n4", ElementType.execute, null, "context.result=333 + c;");
        chain.addNode("n5", "n5", ElementType.stop);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5");

        FlowExecutor chainExecutor = new FlowExecutor();

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }
}