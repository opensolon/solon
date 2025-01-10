package demo.solon.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.ScriptFlowDriver;

/**
 * @author noear 2025/1/10 created
 */
public class ScriptTest {
    @Test
    public void case1() throws Throwable {
        Chain chain = new Chain("c1", "c1", new ScriptFlowDriver(){
            @Override
            public void handleTask(ChainContext context, Task task) throws Throwable {
                System.out.println(task);
                super.handleTask(context, task);
            }
        });

        chain.addNode("n1", "n1", ElementType.start);
        chain.addNode("n2", "n2", ElementType.execute, "111 + a");
        chain.addNode("n3", "n3", ElementType.execute, "222 + b");
        chain.addNode("n4", "n4", ElementType.execute, "333 + c");
        chain.addNode("n5", "n5", ElementType.stop);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5", "a=1 && b=1");

        FlowExecutor chainExecutor = new FlowExecutor();

        ChainContext context = new ChainContext();
        context.set("a", 2);
        context.set("b", 3);
        context.set("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.set("a", 12);
        context.set("b", 13);
        context.set("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }

    @Test
    public void case2() throws Throwable {
        Chain chain = Chain.parse(ResourceUtil.getResourceAsString("expr.json"));

        FlowExecutor chainExecutor = new FlowExecutor();

        ChainContext context = new ChainContext();
        context.set("a", 2);
        context.set("b", 3);
        context.set("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.set("a", 12);
        context.set("b", 13);
        context.set("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }
}