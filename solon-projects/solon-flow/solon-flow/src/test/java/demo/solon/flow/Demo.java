package demo.solon.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.ElementType;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainExecutor;

/**
 * @author noear 2025/1/10 created
 */
public class Demo {
    @Test
    public void case1() throws Exception {
        Chain chain = new Chain("c1", "c1");

        chain.addNode("n1", "n1", ElementType.start, null);
        chain.addNode("n2", "n2", ElementType.execute, "111");
        chain.addNode("n3", "n3", ElementType.execute, "222");
        chain.addNode("n4", "n4", ElementType.execute, "333");
        chain.addNode("n5", "n5", ElementType.stop, null);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5");

        ChainExecutor chainExecutor = new ChainExecutor();

        //完整执行
        chainExecutor.exec(new ChainContextImpl(), chain);
        System.out.println("------------");

        //执行一层
        chainExecutor.exec(new ChainContextImpl(), chain, "n2",1);
    }
}
