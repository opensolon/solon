package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.SimpleFlowDriver;

import java.util.Arrays;

/**
 * @author noear 2025/1/10 created
 */
public class ScriptJavaTest {
    @Test
    public void case1() throws Throwable {
        Chain chain = new Chain("c1", "c1", new SimpleFlowDriver() {
            @Override
            public void handleTask(ChainContext context, Task task) throws Throwable {
                System.out.println(task.node());
                super.handleTask(context, task);
            }
        });

        chain.addNode("n1", "n1", NodeType.start, Arrays.asList(new LinkDecl("n2")));
        chain.addNode("n2", "n2", NodeType.execute, Arrays.asList(new LinkDecl("n3")), null, "context.result=111 + a;");
        chain.addNode("n3", "n3", NodeType.execute, Arrays.asList(new LinkDecl("n4")), null, "context.result=222 + b;");
        chain.addNode("n4", "n4", NodeType.execute,Arrays.asList(new LinkDecl("n5")),  null, "context.result=333 + c;");
        chain.addNode("n5", "n5", NodeType.end, null);

        FlowEngine chainExecutor = new FlowEngine();

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