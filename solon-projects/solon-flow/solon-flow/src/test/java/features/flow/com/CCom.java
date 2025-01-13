package features.flow.com;

import lombok.extern.slf4j.Slf4j;
import org.noear.solon.annotation.Component;
import org.noear.solon.flow.Node;
import org.noear.solon.flow.TaskComponent;
import org.noear.solon.flow.Context;

/**
 * @author noear 2025/1/11 created
 */
@Slf4j
@Component("c")
public class CCom implements TaskComponent {
    @Override
    public void run(Context context, Node node) {
        log.info("CCom");
    }
}
