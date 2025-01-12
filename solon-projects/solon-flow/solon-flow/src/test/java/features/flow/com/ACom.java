package features.flow.com;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.flow.core.TaskComponent;
import org.noear.solon.flow.core.ChainContext;

/**
 * @author noear 2025/1/11 created
 */
@Slf4j
@Component("a")
public class ACom implements TaskComponent {
    @Override
    public void run(ChainContext context) {
        log.info("ACom");
    }
}
