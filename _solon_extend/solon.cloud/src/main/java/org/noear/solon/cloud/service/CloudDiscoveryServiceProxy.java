package org.noear.solon.cloud.service;

import org.noear.solon.Utils;
import org.noear.solon.cloud.CloudClient;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;

/**
 * 云端注册与发现服务代理
 *
 * @author noear
 * @since 2.2
 */
public class CloudDiscoveryServiceProxy implements  CloudDiscoveryService {
    CloudDiscoveryService real;

    public CloudDiscoveryServiceProxy(CloudDiscoveryService real) {
        this.real = real;
    }

    @Override
    public void register(String group, Instance instance) {
        real.register(group, instance);
    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {
        real.registerState(group, instance, health);
    }

    @Override
    public void deregister(String group, Instance instance) {
        real.deregister(group, instance);
    }

    @Override
    public Discovery find(String group, String service) {
        Discovery discovery = real.find(group, service);

        //尝试增加发现代理
        if (Utils.isEmpty(discovery.agent())) {
            if (CloudClient.config() != null) {
                //前缀在前，方便相同配置在一起
                String agent = CloudClient.config().pull(group, "discovery.agent." + service).value();

                if (Utils.isNotEmpty(agent)) {
                    discovery.agent(agent);
                }
            }
        }

        return discovery;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        real.attention(group, service, observer);
    }
}
