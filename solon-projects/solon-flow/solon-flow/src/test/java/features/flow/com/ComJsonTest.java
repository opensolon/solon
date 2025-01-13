package features.flow.com;

import org.junit.jupiter.api.Test;
import org.noear.solon.SimpleSolonApp;
import org.noear.solon.flow.Chain;
import org.noear.solon.flow.Context;
import org.noear.solon.flow.FlowEngine;

/**
 * @author noear 2025/1/10 created
 */
public class ComJsonTest {
    private FlowEngine flowEngine = FlowEngine.newInstance();

    @Test
    public void case1() throws Throwable {
        SimpleSolonApp solonApp = new SimpleSolonApp(ComJsonTest.class);
        solonApp.start(null);

        Chain chain = Chain.parseByUri("classpath:flow/com.json");

        Context context = new Context();
        context.paramSet("a", 2);
        context.paramSet("b", 3);
        context.paramSet("c", 4);

        //完整执行

        flowEngine.eval(chain, context);
        System.out.println("------------");

        context = new Context();
        context.paramSet("a", 12);
        context.paramSet("b", 13);
        context.paramSet("c", 14);

        //执行一层
        flowEngine.eval(chain, "n2", 1, context);
    }
}