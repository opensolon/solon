package features.flow.com;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.core.util.ResourceUtil;
import org.noear.solon.flow.core.*;
import org.noear.solon.flow.driver.SimpleFlowDriver;

import java.util.HashMap;

/**
 * @author noear 2025/1/10 created
 */
public class ComTest {
    @Test
    public void case1() throws Throwable {
        SimpleSolonApp solonApp = new SimpleSolonApp(ComTest.class);
        solonApp.start(null);

        Chain chain = new Chain("c1", "c1", new SimpleFlowDriver(){
            @Override
            public void handleTask(ChainContext context, Task task) throws Throwable {
                context.result = task.node().id();
                if(task.node().id().equals("n3")) {
                    context.interrupt(true);
                }

                System.out.println(task.node());
                super.handleTask(context, task);
            }
        });

        chain.addNode("n1", "n1", ElementType.start);
        chain.addNode("n2", "n2", ElementType.execute, new HashMap<>(), "@a");
        chain.addNode("n3", "n3", ElementType.execute,null, "@b");
        chain.addNode("n4", "n4", ElementType.execute,null, "@c");
        chain.addNode("n5", "n5", ElementType.end);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5");

        FlowEngine flowEngine = new FlowEngine();

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.exec(context, chain);

        assert "n3".equals(context.result);

        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.exec(context, chain, "n2", 1);


        assert "n2".equals(context.result);
    }

    @Test
    public void case2() throws Throwable {
        SimpleSolonApp solonApp = new SimpleSolonApp(ComTest.class);
        solonApp.start(null);

        Chain chain = Chain.parse(ResourceUtil.getResourceAsString("com.json"));

        FlowEngine flowEngine = new FlowEngine();

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.exec(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.exec(context, chain, "n2", 1);
    }
}