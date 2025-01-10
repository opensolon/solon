package features.flow.com;

import org.noear.solon.annotation.Component;
import org.noear.solon.flow.TaskComponent;
import org.noear.solon.flow.core.ChainContext;

/**
 * @author noear 2025/1/11 created
 */

@Component("a")
public class ACom implements TaskComponent {
    @Override
    public void run(ChainContext context) {
        System.out.println("ACom");
    }
}
