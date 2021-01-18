package org.noear.solon.cloud.extend.water.service;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
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
                Node node = new Node();
                node.address = t1.address;
                node.meta = t1.meta;
                node.weight = t1.weight;
                node.protocol = t1.protocol;

                d2.cluster.add(node);
            });

            return d2;
        }
    }
}
