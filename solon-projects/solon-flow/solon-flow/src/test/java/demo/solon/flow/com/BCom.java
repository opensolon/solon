package demo.solon.flow.com;

import org.noear.solon.annotation.Component;
import org.noear.solon.flow.core.ChainContext;
import org.noear.solon.flow.TaskComponent;

/**
 * @author noear 2025/1/11 created
 */
@Component("b")
public class BCom implements TaskComponent {
    @Override
    public void run(ChainContext context) {
        System.out.println("BCom");
    }
}
