package demo.solon.flow;

import org.junit.jupiter.api.Test;
import org.noear.solon.flow.core.ElementType;
import org.noear.solon.flow.core.Chain;
import org.noear.solon.flow.core.ChainExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear 2025/1/10 created
 */
public class Demo {
    @Test
    public void case1() throws Exception {
        Chain chain = new Chain("c1", "c1");

        chain.addNode("n1", "n1", ElementType.start);
        chain.addNode("n2", "n2", ElementType.execute, "111 + 1");
        chain.addNode("n3", "n3", ElementType.execute, "222 + a");
        chain.addNode("n4", "n4", ElementType.execute, "333 + b");
        chain.addNode("n5", "n5", ElementType.stop);
        chain.addLine("l1", "l1", "n1", "n2");
        chain.addLine("l2", "l2", "n2", "n3");
        chain.addLine("l3", "l3", "n3", "n4");
        chain.addLine("l4", "l4", "n4", "n5", "a=1 && b=1");

        ChainExecutor chainExecutor = new ChainExecutor();

        Map<String, Object> model = new HashMap<>();
        model.put("a", 2);
        model.put("b", 3);

        //完整执行

        chainExecutor.exec(new ChainContextImpl(model), chain);
        System.out.println("------------");

        //执行一层
        chainExecutor.exec(new ChainContextImpl(model), chain, "n2", 1);
    }
}
