package features.flow.com;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.SimpleFlowDriver;

/**
 * @author noear 2025/1/10 created
 */
@Slf4j
public class ComJavaTest {
    private FlowEngine flowEngine = FlowEngine.newInstance();

    @Test
    public void case1() throws Throwable {
        SimpleSolonApp solonApp = new SimpleSolonApp(ComJavaTest.class);
        solonApp.start(null);

        Chain chain = new Chain("c1", "c1", new SimpleFlowDriver() {
            @Override
            public void handleTask(ChainContext context, Task task) throws Throwable {
                context.result = task.node().id();
                if (task.node().id().equals("n3")) {
                    context.interrupt();
                }

                super.handleTask(context, task);
            }
        });


        chain.addNode(new NodeDecl("n1", NodeType.start).linkAdd("n2"));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("@a").linkAdd("n3"));
        chain.addNode(new NodeDecl("n3", NodeType.execute).task("@b").linkAdd("n4"));
        chain.addNode(new NodeDecl("n4", NodeType.execute).task("@c").linkAdd("n5"));
        chain.addNode(new NodeDecl("n5", NodeType.end));

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.eval(chain, context);

        assert "n3".equals(context.result);

        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.eval(chain, "n2", 1, context);


        assert "n2".equals(context.result);
    }
}