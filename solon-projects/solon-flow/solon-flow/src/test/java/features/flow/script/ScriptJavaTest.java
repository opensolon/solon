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

        chain.addNode(new NodeDecl("n1", NodeType.start).link(new LinkDecl("n2")));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("context.result=111 + a;").link(new LinkDecl("n3")));
        chain.addNode(new NodeDecl("n3", NodeType.execute).task("context.result=222 + b;").link(new LinkDecl("n4")));
        chain.addNode(new NodeDecl("n4", NodeType.execute).task("context.result=333 + c;").link(new LinkDecl("n5")));
        chain.addNode(new NodeDecl("n5", NodeType.end));

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