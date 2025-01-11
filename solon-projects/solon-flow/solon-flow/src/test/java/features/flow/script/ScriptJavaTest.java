package features.flow.script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.SimpleFlowDriver;

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

        chain.addNode(new NodeDecl("n1", NodeType.start).linkTo("n2"));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("context.result=111 + a;").linkTo("n3"));
        chain.addNode(new NodeDecl("n3", NodeType.end));

        FlowEngine flowEngine = new FlowEngine();

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        flowEngine.eval(context, chain);
    }
}