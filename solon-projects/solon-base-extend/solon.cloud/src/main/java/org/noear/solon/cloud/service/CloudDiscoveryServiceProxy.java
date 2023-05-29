package org.noear.solon.cloud.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.utils.DiscoveryUtils;

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
        DiscoveryUtils.tryLoadAgent(discovery, group, service);

        return discovery;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {
        real.attention(group, service, observer);
    }
}
