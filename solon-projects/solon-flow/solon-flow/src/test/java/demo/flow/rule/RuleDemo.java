package demo.flow.rule;

import org.noear.solon.annotation.Inject;
import org.noear.solon.flow.ChainContext;
import org.noear.solon.flow.FlowEngine;

/**
 * @author noear 2025/1/24 created
 */
public class RuleDemo {
    @Inject
    private FlowEngine flowEngine;

    public void demo() throws Throwable {
        flowEngine.eval("r1", new ChainContext().put("order", new Order()));
    }
}
