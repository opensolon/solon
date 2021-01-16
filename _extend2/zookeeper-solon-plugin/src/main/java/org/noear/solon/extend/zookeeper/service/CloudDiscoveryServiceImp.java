package org.noear.solon.extend.zookeeper.service;

import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Node;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import java.util.function.Consumer;

/**
 * @author noear 2021/1/15 created
 */
public class CloudDiscoveryServiceImp implements CloudDiscoveryService {

    @Override
    public void register(Node instance) {

    }

    @Override
    public void deregister(Node instance) {

    }

    @Override
    public Discovery find(String service) {
        return null;
    }

    @Override
    public void attention(String service, Consumer<Discovery> observer) {

    }
}
