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
        Chain chain = new Chain();
        chain.addNode("n1", "n1", ElementType.start, null);
        chain.addNode("n2", "n2", ElementType.execute, "F,1+1");
        chain.addNode("n3", "n3", ElementType.stop, null);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");

        ChainExecutor chainExecutor = new ChainExecutor();
        chainExecutor.exec(new ChainContextImpl(), chain);
    }

    public void case2() throws Exception {
        /**
         * Chain{
         *     Start:N->Execute:N->Line->Exclusive:N->(C)Line
         *                                          ->(C)Line->Execute:N
         * }
         *
         * */
    }
}
