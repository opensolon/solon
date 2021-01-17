package org.noear.solon.extend.water.service;

import org.noear.snack.ONode;
import org.noear.solon.Solon;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudDiscoveryService;
import org.noear.water.WaterClient;

/**
 * @author noear 2021/1/17 created
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {
    @Override
    public void register(Node instance) {
        String service = instance.service;
        String address = instance.ip + ":" + instance.port;
        String meta = null;
        if (instance.meta != null) {
            meta = ONode.stringify(instance.meta);
        }

        WaterClient.Registry.register(service, address, meta, Solon.cfg().isDriftMode());
    }

    @Override
    public void deregister(Node instance) {

    }

    @Override
    public Discovery find(String service) {
        return null;
    }

    @Override
    public void attention(String service, CloudDiscoveryHandler observer) {

    }
}
