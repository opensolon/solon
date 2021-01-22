package org.noear.solon.cloud.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.water.WaterClient;
import org.noear.water.model.DiscoverM;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册与发现服务
 *
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {
    @Override
    public void register(String group, Instance instance) {
        String meta = null;
        if (instance.meta != null) {
            meta = ONode.stringify(instance.meta);
        }

        WaterClient.Registry.register(instance.service, instance.address, meta, Solon.cfg().isDriftMode());
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        String meta = null;
        if (instance.meta != null) {
            meta = ONode.stringify(instance.meta);
        }

        WaterClient.Registry.set(instance.service, instance.address, meta, health);
    }

    @Override
    public void deregister(String group, Instance instance) {
        String meta = null;
        if (instance.meta != null) {
            meta = ONode.stringify(instance.meta);
        }

        WaterClient.Registry.unregister(instance.service, instance.address, meta);
    }

    @Override
    public Discovery find(String group, String service) {
        DiscoverM d1 = WaterClient.Registry.discover(service, Instance.local().service, Instance.local().address);
        return ConvertUtil.from(service, d1);
    }

    Map<CloudDiscoveryHandler, CloudDiscoveryObserverEntity> observerMap = new HashMap<>();

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        observerMap.put(observer, new CloudDiscoveryObserverEntity(group, service, observer));
    }

    public void onUpdate(String group, String service) {
        Discovery discovery = find(group, service);

        observerMap.forEach((k, v) -> {
            if (service.equals(v.service)) {
                v.handler(discovery);
            }
        });
    }
}
