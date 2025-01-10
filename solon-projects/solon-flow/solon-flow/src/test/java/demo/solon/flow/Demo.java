package demo.solon.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.NodeType;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainExecutor;

/**
 * @author noear 2025/1/10 created
 */
public class Demo {
    @Test
    public void case1() throws Exception {
        Chain chain = new Chain();
        chain.addNode("n1", "n1", NodeType.start, null, null, null, null);
        chain.addNode("n2", "n2", NodeType.execute, null, null, "a.1==1", "F,1+1");
        chain.addNode("n3", "n3", NodeType.stop, null, null, null, null);
        chain.addNode("l1", "l1", NodeType.line, "n1", "n2", null, null);
        chain.addNode("l2", "l2", NodeType.line, "n2", "n3", null, null);

        ChainExecutor chainExecutor = new ChainExecutor();
        chainExecutor.exec(chain, new ChainContextImpl());
    }
}
