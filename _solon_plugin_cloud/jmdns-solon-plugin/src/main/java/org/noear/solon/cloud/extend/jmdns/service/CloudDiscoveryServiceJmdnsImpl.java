package org.noear.solon.cloud.extend.jmdns.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryService;

/**
 * @author noear 2022/9/14 created
 */
public class CloudDiscoveryServiceJmdnsImpl implements CloudDiscoveryService {
    @Override
    public void register(String group, Instance instance) {

    }

    @Override
    public void registerState(String group, Instance instance, boolean health) {

    }

    @Override
    public void deregister(String group, Instance instance) {

    }

    @Override
    public Discovery find(String group, String service) {
        return null;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {

    }
}
