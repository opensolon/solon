package demo.flow.async;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.FlowEngine;
import org.noear.solon.flow.NodeDecl;
import org.noear.solon.flow.NodeType;
import org.noear.solon.flow.driver.MapChainDriver;

/**
 * @author noear 2025/3/20 created
 */
public class AsyncTest {
    @Test
    public void case1() throws Throwable {
        FlowEngine engine = FlowEngine.newInstance();
        engine.register(MapChainDriver.getInstance());

        MapChainDriver.getInstance().putComponent("a", new AsyncTaskComponent());

        Chain chain = new Chain("c1", "c1");
        chain.addNode(new NodeDecl("n1", NodeType.execute).task("@a").linkAdd("n2"));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("@a").linkAdd("n3"));
        chain.addNode(new NodeDecl("n3", NodeType.execute).task("@a"));
        chain.check();

        engine.eval(chain);

        //因为是异步，所以要阻一下
        System.in.read();
    }
}
