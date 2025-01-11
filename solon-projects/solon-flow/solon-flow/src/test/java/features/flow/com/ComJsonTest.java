package features.flow.com;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.flow.core.*;

/**
 * @author noear 2025/1/10 created
 */
public class ComJsonTest {
    private FlowEngine flowEngine =  FlowEngine.newInstance();

    @Test
    public void case1() throws Throwable {
        SimpleSolonApp solonApp = new SimpleSolonApp(ComJsonTest.class);
        solonApp.start(null);

        Chain chain = Chain.parseByUri("classpath:com.json");

        ChainContext context = new ChainContext();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.eval(context, chain);
        System.out.println("------------");

        context = new ChainContext();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.eval(context, chain, "n2", 1);
    }
}