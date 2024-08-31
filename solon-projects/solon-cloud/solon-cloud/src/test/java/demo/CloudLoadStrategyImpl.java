package demo;

import org.noear.solon.annotation.Component;
import org.noear.solon.cloud.impl.CloudLoadStrategy;
import org.noear.solon.cloud.impl.CloudLoadStrategyDefault;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * @author noear 2024/8/5 created
 */
@Component
public class CloudLoadStrategyImpl implements CloudLoadStrategy {
    private static CloudLoadStrategy def = new CloudLoadStrategyDefault();

    @Override
    public String getServer(Discovery discovery, int port) {
        for (Instance i1 : discovery.cluster()) {
            //也可以通过 tags 过滤；
            //结合 ctx = Context.current()，可根据请求信息进行过滤
            if ("v1".equals(i1.metaGet("ver"))) {
                return i1.uri();
            }
        }

        return def.getServer(discovery, port);
    }
}