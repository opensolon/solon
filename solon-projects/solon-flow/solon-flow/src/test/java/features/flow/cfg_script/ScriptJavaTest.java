package features.flow.cfg_script;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.*;

/**
 * 手动配装风格
 *
 * @author noear 2025/1/10 created
 */
public class ScriptJavaTest {
    private FlowEngine flowEngine = FlowEngine.newInstance();

    @Test
    public void case1() throws Throwable {
        Chain chain = new Chain("c1");

        chain.addNode(new NodeDecl("n1", NodeType.start).linkAdd("n2"));
        chain.addNode(new NodeDecl("n2", NodeType.execute).task("context.result=111 + a;").linkAdd("n3"));
        chain.addNode(new NodeDecl("n3", NodeType.end));


        ChainContext context = new ChainContext();
        context.put("a", 2);
        context.put("b", 3);
        context.put("c", 4);

        flowEngine.eval(chain, context);
    }
}