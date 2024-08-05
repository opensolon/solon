package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.impl.CloudLoadStrategy;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * @author noear 2024/8/5 created
 */
@Component
public class CloudLoadStrategyImpl implements CloudLoadStrategy {
    @Override
    public String getServer(Discovery discovery) {
        for (Instance i1 : discovery.cluster()) {
            if ("v1".equals(i1.metaGet("ver"))) { //
                return i1.uri();
            }
        }

        return null;
    }
}
