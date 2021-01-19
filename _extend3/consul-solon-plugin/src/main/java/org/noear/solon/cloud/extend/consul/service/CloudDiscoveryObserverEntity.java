package org.noear.solon.cloud.extend.consul.service;

import org.noear.solon.cloud.CloudDiscoveryHandler;
import org.noear.solon.cloud.model.Discovery;

/**
 * @author noear
 * @since 1.2
 */
public class CloudDiscoveryObserverEntity implements CloudDiscoveryHandler {
    public String group;
    public String service;
    public CloudDiscoveryHandler handler;

    public CloudDiscoveryObserverEntity(String group, String service, CloudDiscoveryHandler handler) {
        this.group = group;
        this.service = service;
        this.handler = handler;
    }

    @Override
    public void handler(Discovery discovery) {
        handler.handler(discovery);
    }
}
