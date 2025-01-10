package demo.solon.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.ElementType;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.ChainExecutor;

/**
 * @author noear 2025/1/10 created
 */
public class Demo {
    @Test
    public void case1() throws Exception {
        Chain chain = new Chain();
        chain.addNode("n1", "n1", ElementType.start, null);
        chain.addNode("n2", "n2", ElementType.execute, "F,1+1");
        chain.addNode("n3", "n3", ElementType.stop, null);
        chain.addLine("l1", "l1", "n1", "n2", null);
        chain.addLine("l2", "l2", "n2", "n3", null);

        ChainExecutor chainExecutor = new ChainExecutor();
        chainExecutor.exec(chain, new ChainContextImpl());
    }
}
