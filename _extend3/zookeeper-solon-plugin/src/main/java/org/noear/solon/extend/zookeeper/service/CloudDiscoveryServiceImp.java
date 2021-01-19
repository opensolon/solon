package org.noear.solon.extend.zookeeper.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudDiscoveryService;

/**
 * @author noear 2021/1/15 created
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {

    @Override
    public void register(String group, Node instance) {

    }

    @Override
    public void deregister(String group, Node instance) {

    }

    @Override
    public Discovery find(String group, String service) {
        return null;
    }

    @Override
    public void attention(String group, String service, CloudDiscoveryHandler observer) {

    }
}
