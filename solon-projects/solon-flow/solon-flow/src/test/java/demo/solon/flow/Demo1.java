package demo.solon.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.ElementType;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.FlowContext;
import org.noear.solon.flow.core.FlowExecutor;

/**
 * @author noear 2025/1/10 created
 */
public class Demo1 {
    @Test
    public void case1() throws Exception {
        Chain chain = new Chain("c1", "c1");

        chain.addNode("n1", "n1", ElementType.start);
        chain.addNode("n2", "n2", ElementType.execute, "111 + a");
        chain.addNode("n3", "n3", ElementType.execute, "222 + b");
        chain.addNode("n4", "n4", ElementType.execute, "333 + c");
        chain.addNode("n5", "n5", ElementType.stop);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5", "a=1 && b=1");

        FlowExecutor chainExecutor = new FlowExecutor(new Demo1FlowDriver());

        FlowContext context = new FlowContext();
        context.set("a", 2);
        context.set("b", 3);
        context.set("c", 4);

        //完整执行

        chainExecutor.exec(context, chain);
        System.out.println("------------");

        context = new FlowContext();
        context.set("a", 12);
        context.set("b", 13);
        context.set("c", 14);

        //执行一层
        chainExecutor.exec(context, chain, "n2", 1);
    }
}