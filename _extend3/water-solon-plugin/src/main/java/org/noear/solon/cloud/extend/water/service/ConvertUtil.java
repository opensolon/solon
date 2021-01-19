package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.water.model.DiscoverM;

import java.util.ArrayList;

/**
 * @author noear
 * @since 1.2
 */
public class ConvertUtil {
    public static Discovery from(String service, DiscoverM d1) {
        if (d1 == null) {
            return null;
        } else {
            Discovery d2 = new Discovery(service);
            d2.agent = d1.url;
            d2.policy = d1.policy;
            d2.cluster = new ArrayList<>();

            d1.list.forEach((t1) -> {
                Instance instance = new Instance();
                instance.address = t1.address;
                instance.weight = t1.weight;
                instance.protocol = t1.protocol;

                d2.cluster.add(instance);
            });

            return d2;
        }
    }
}
