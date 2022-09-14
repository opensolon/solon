package org.noear.solon.cloud.extend.jmdns.service;

import org.noear.snack.ONode;
import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;
import org.noear.solon.cloud.model.Instance;
import org.noear.solon.cloud.service.CloudDiscoveryService;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.URI;

/**
 * @author noear 2022/9/14 created
 */
public class CloudDiscoveryServiceJmdnsImpl implements CloudDiscoveryService {
    @Override
    public void register(String group, Instance instance) {
        try {
            JmDNS jmDNS = JmDNS.create();

            String fullyQualitifed = group + ".local.";
            String values = ONode.stringify(instance);
            URI uri = URI.create(instance.address());

            jmDNS.registerService(ServiceInfo.create(fullyQualitifed, instance.service(), uri.getPort(), values));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
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
