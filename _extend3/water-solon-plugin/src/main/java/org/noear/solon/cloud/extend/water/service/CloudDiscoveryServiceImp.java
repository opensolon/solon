package org.noear.solon.cloud.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.water.WaterClient;
import org.noear.water.model.DiscoverM;

import java.util.HashMap;
import java.util.Map;

/**
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {
    @Override
    public void register(Node instance) {
        WaterClient.Registry.register(instance.service, instance.address, instance.meta, Solon.cfg().isDriftMode());
    }

    @Override
    public void deregister(Node instance) {
        String meta = null;
        if (instance.meta != null) {
            meta = ONode.stringify(instance.meta);
        }

        WaterClient.Registry.unregister(instance.service, instance.address, meta);
    }

    @Override
    public Discovery find(String service) {
        DiscoverM d1 = WaterClient.Registry.discover(service, Solon.cfg().appName(), Node.local().address);
        return ConvertUtil.from(service, d1);
    }

    Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(service, observer));
    }
}
