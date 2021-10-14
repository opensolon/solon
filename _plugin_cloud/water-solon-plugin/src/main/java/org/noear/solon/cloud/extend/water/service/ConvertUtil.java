package org.noear.solon.cloud.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Utils;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.water.model.DiscoverM;

import java.util.Map;

/**
 * 转换工具
 *
 * @author noear
 * @since 1.2
 */
class ConvertUtil {
    public static Discovery from(String service, DiscoverM d1) {
        if (d1 == null) {
            return null;
        } else {
            Discovery d2 = new Discovery(service);
            d2.agent(d1.url);
            d2.policy(d1.policy);

            d1.list.forEach((t1) -> {
                Map<String,String> meta =null;
                if(Utils.isNotEmpty(t1.meta)) {
                    meta = ONode.deserialize(t1.meta);
                }

                Instance instance = new Instance(service, t1.address)
                        .protocol(t1.protocol)
                        .weight(t1.weight)
                        .metaPutAll(meta);

                d2.instanceAdd(instance);
            });

            return d2;
        }
    }
}
